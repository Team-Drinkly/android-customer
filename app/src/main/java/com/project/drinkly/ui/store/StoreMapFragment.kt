package com.project.drinkly.ui.store

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.sdk.auth.TokenManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.project.drinkly.BuildConfig
import com.project.drinkly.R
import com.project.drinkly.api.InfoManager
import com.project.drinkly.api.request.login.FcmTokenRequest
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.databinding.FragmentStoreMapBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.DialogEvent
import com.project.drinkly.ui.onboarding.viewModel.LoginViewModel
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel
import com.project.drinkly.util.MainUtil.formatDistance
import com.project.drinkly.util.MyApplication
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignStart


class StoreMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentStoreMapBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: StoreViewModel by lazy { ViewModelProvider(requireActivity())[StoreViewModel::class.java] }
    private val loginViewModel: LoginViewModel by lazy { ViewModelProvider(requireActivity())[LoginViewModel::class.java] }

    private val markers = mutableListOf<Marker>()
    private var lastCameraPosition: LatLng? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStoreMapBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        mapInit()
        checkNotificationInfo()
        showToolTip()

        // 버튼 클릭 시 현재 위치로 이동
        mainActivity.binding.run {
            buttonMyLocation.setOnClickListener {
                mixpanel.track("click_home_gps", null)

                checkLocationPermission()
            }

            buttonList.run {
                setImageResource(R.drawable.ic_list)
                setOnClickListener {
                    mixpanel.track("click_home_listmap", null)

                    // 제휴업체 - 리스트 화면으로 전환
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, StoreListFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        return binding.root
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        observeViewModel()
        mapSetup()
        checkLocationPermission()
    }

    private fun mapInit() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        binding.mapView.apply {
            onCreate(null)
            getMapAsync(this@StoreMapFragment)
        }

        NaverMapSdk.getInstance(mainActivity).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.MAP_API_KEY)
    }

    private fun mapSetup() {
        naverMap.run {
            mapType = NaverMap.MapType.Navi // 네비게이션 모드 (다크 모드)
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            isIndoorEnabled = true
            isNightModeEnabled = true
            locationSource = this@StoreMapFragment.locationSource
            locationTrackingMode = LocationTrackingMode.None
            maxZoom = 18.0
            minZoom = 8.0
            uiSettings.apply {
                setLogoMargin(40, 0, 40, 320)
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
                isZoomControlEnabled = false
                isCompassEnabled = false
                isScaleBarEnabled = false
            }
            addOnCameraIdleListener { handleCameraIdle() }
        }
    }

    private fun handleCameraIdle() {
        val currentCenter = naverMap.cameraPosition.target
        if (lastCameraPosition == currentCenter) return
        lastCameraPosition = currentCenter
    }

    // [Location & Permissions]
    private fun checkLocationPermission() {
        if (!locationSource.isActivated) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            moveToCurrentLocation()
            requestNotificationPermissionIfNeeded()
        }
    }

    private fun moveToCurrentLocation() {
        val fallback = LatLng(MyApplication.latitude, MyApplication.longitude)
        naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(fallback, 15.0).animate(CameraAnimation.Fly))
        viewModel.getStoreList(mainActivity, fallback.latitude.toString(), fallback.longitude.toString(), 10000, null)
    }

    private fun getCurrentLocationAndCallApi() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            numUpdates = 1
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        MyApplication.latitude = location.latitude
                        MyApplication.longitude = location.longitude

                        Log.d("DrinklyLog", "💡 즉시 위치 확인: ${location.latitude}, ${location.longitude}")
                        moveToCurrentLocation()
                    } else {
                        setFallbackLocation()
                    }
                }
            },
            null
        )
    }

    private fun setFallbackLocation() {
        MyApplication.latitude = 37.63022195215973
        MyApplication.longitude = 127.07671771357782
    }


    private fun requestNotificationPermissionIfNeeded() {
        if (!MyApplication.preferences.isNotificationPermissionChecked() && MyApplication.isLogin) {
            checkAndRequestNotificationPermission()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        } else {
            sendFcmToken(true)
        }
    }

    private fun sendFcmToken(allowed: Boolean) {
        MyApplication.preferences.setNotificationPermissionChecked(true)
        val token = MyApplication.preferences.getFCMToken().orEmpty()
        val body = FcmTokenRequest(InfoManager(mainActivity).getUserId()?.toInt() ?: 0, allowed, token, "ANDROID")
        loginViewModel.saveFcmToken(mainActivity, body)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            getCurrentLocationAndCallApi()
            if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                moveToCurrentLocation()
                requestNotificationPermissionIfNeeded()
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            val allowed = grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
            sendFcmToken(allowed)
        }
    }

    // [Markers & Observers]
    private fun observeViewModel() {
        viewModel.storeInfo.observe(viewLifecycleOwner) { storeList ->
            markers.forEach { it.map = null }
            markers.clear()

            storeList.forEachIndexed { index, store ->
                val marker = Marker().apply {
                    position = LatLng(store.latitude?.toDouble() ?: 0.0, store.longitude?.toDouble() ?: 0.0)
                    icon = OverlayImage.fromResource(if (store.isAvailable == true) R.drawable.ic_marker_enabled else R.drawable.ic_marker_disabled)
                    map = naverMap
                    setOnClickListener {
                        showStoreDetail(store)
                        true
                    }
                }
                markers.add(marker)
            }

            naverMap.setOnMapClickListener { _, _ ->
                hideStoreBottomSheet()
            }
        }
    }

    private fun showStoreDetail(store: StoreListResponse) {
        mixpanel.track("click_home_map_pin", null)

        mainActivity.run {
            hideBottomNavigation(true)
            hideMyLocationButton(true)
            hideMapButton(true)
        }

        val position = LatLng(store.latitude?.toDouble() ?: 0.0, store.longitude?.toDouble() ?: 0.0)
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(position, 15.0).animate(CameraAnimation.Fly)
        naverMap.moveCamera(cameraUpdate)

        binding.bottomSheetStoreList.apply {
            layoutStoreList.visibility = View.VISIBLE

            textViewStoreName.text = store.storeName
            textViewStoreCall.text = store.storeTel
            textViewStoreAvailableDrink.text = store.availableDrinks?.joinToString(",")
            textViewStoreIsOpen.text = store.isOpen
            textViewStoreCloseOrOpenTime.text = store.openingInfo
            textViewDistance.text = formatDistance(store.distance)
            textViewDistance.visibility = if (NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()) View.VISIBLE else View.GONE

            imageViewStore.setImageResource(R.drawable.img_store_main_basic)
            store.storeMainImageUrl?.let {
                Glide.with(mainActivity).load(it).into(imageViewStore)
            }

            layoutStoreUnavailable.visibility = if (store.isAvailable == true) View.INVISIBLE else View.VISIBLE

            layoutStoreList.setOnClickListener {
                mixpanel.track("move_map_to_detail", null)

                val bundle = Bundle().apply { putLong("storeId", store.id) }
                val detailFragment = StoreDetailFragment().apply { arguments = bundle }
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun hideStoreBottomSheet() {
        binding.bottomSheetStoreList.layoutStoreList.visibility = View.GONE
        mainActivity.run {
            hideBottomNavigation(false)
            hideMyLocationButton(false)
            hideMapButton(false)
        }
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()

        binding.bottomSheetStoreList.layoutStoreList.visibility = View.GONE
        mainActivity.run {
            hideBottomNavigation(false)
            hideMyLocationButton(false)
            hideMapButton(false)
            hideOrderHistoryButton(true)
        }

        checkPermissionsAndSendToMixpanel(mainActivity)
    }

    private fun showToolTip() {
        val balloon = Balloon.Builder(mainActivity)
            .setWidthRatio(0.6f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("리스트로 매장을 확인할 수 있어요")
            .setTextColorResource(R.color.gray1)
            .setTextSize(14f)
            .setTextTypeface(ResourcesCompat.getFont(mainActivity, R.font.pretendard_medium)!!)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowOrientation(ArrowOrientation.END)
            .setArrowSize(5)
            .setArrowPosition(0.5f)
            .setArrowColorResource(R.color.dialog_background)
            .setTextGravity(Gravity.CENTER)
            .setElevation(0)
            .setPaddingHorizontal(12)
            .setPaddingVertical(8)
            .setMarginHorizontal(5)
            .setCornerRadius(8f)
            .setBackgroundDrawableResource(R.drawable.background_tooltip_dialog_background)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .build()

        mainActivity.binding.buttonList.showAlignStart(balloon)
        Handler().postDelayed({ balloon.dismiss() }, 3000)
    }

    override fun onStart() { super.onStart(); binding.mapView.onStart() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
    override fun onStop() { super.onStop(); binding.mapView.onStop() }
    override fun onDestroy() { super.onDestroy(); binding.mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    private fun checkNotificationInfo() {
        mainActivity.getPendingPushStoreId()?.takeIf { MyApplication.isLogin }?.let {
            val bundle = Bundle().apply { putLong("storeId", it) }
            val fragment = StoreDetailFragment().apply { arguments = bundle }
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun checkPermissionsAndSendToMixpanel(context: Context) {
        mixpanel.people.set("notification_permission", if (NotificationManagerCompat.from(context).areNotificationsEnabled()) "Granted" else "Denied")
        mixpanel.people.set("location_permission", if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) "Granted" else "Denied")
    }
}
