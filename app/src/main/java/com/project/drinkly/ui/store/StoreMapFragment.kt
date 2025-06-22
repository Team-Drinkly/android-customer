package com.project.drinkly.ui.store

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import com.project.drinkly.util.MyApplication
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignStart

class StoreMapFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentStoreMapBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StoreViewModel
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap


    private val CURRENT_LOCATION_CODE = 200
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private val NOTIFICATION_PERMISSTION_REQUEST_CODE: Int = 123
    private lateinit var locationSource: FusedLocationSource // 위치를 반환하는 구현체

    var lastCameraPosition: LatLng? = null

    var getStoreInfo = mutableListOf<StoreListResponse>()

    val markers = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreMapBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[StoreViewModel::class.java]

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        observeViewModel()

        binding.run {
            NaverMapSdk.getInstance(mainActivity).client =
                NaverMapSdk.NaverCloudPlatformClient("${com.project.drinkly.BuildConfig.MAP_API_KEY}")

            // 버튼 클릭 시 현재 위치로 이동
            mainActivity.binding.buttonMyLocation.setOnClickListener {
                mixpanel.track("click_home_gps", null)

                checkLocationPermission()
            }

            locationSource = FusedLocationSource(this@StoreMapFragment, LOCATION_PERMISSTION_REQUEST_CODE)
        }

        mainActivity.binding.buttonList.run {
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

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        showToolTip()

        binding.bottomSheetStoreList.layoutStoreList.visibility = View.GONE

        mainActivity.run {
            hideBottomNavigation(false)
            hideMyLocationButton(false)
            hideOrderHistoryButton(true)
            hideMapButton(false)
        }

        checkPermissionsAndSendToMixpanel(mainActivity)

        checkNotificationInfo()
    }

    fun checkNotificationInfo() {
        val storeId = mainActivity.getPendingPushStoreId()
        storeId?.let {
            if(MyApplication.isLogin) {
                var nextFragment = StoreDetailFragment()

                val bundle = Bundle().apply { putLong("storeId", it) }

                // 전달할 Fragment 생성
                nextFragment = StoreDetailFragment().apply {
                    arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, nextFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    fun showToolTip() {
        val balloon = Balloon.Builder(mainActivity)
//                .setWidth(BalloonSizeSpec.WRAP)
            .setWidthRatio(0.6f) // sets width as 60% of the horizontal screen's size.
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("리스트로 매장을 확인할 수 있어요")
            .setTextColorResource(R.color.gray1)
            .setTextSize(14f)
            .setTextTypeface(ResourcesCompat.getFont(mainActivity,R.font.pretendard_medium)!!)
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

        Handler().postDelayed({
            balloon.dismiss()
        }, 3000)
    }

    private fun moveToCurrentLocation() {
        val lastLocation = locationSource.lastLocation
        if (lastLocation != null) {
            // 위치가 유효할 때 카메라 이동
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(
                LatLng(lastLocation.latitude, lastLocation.longitude),
                15.0 // 줌 레벨
            ).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        } else {
            // 위치 요청이 아직 활성화되지 않은 경우 강제 요청
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
            Log.d("MapFragment", "현재 위치 정보를 가져올 수 없어 추적 모드 활성화")
        }
    }


    private fun checkLocationPermission() {
        if (!locationSource.isActivated) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSTION_REQUEST_CODE
            )
        } else {
            moveToCurrentLocation() // 권한이 이미 부여된 경우
            if (!MyApplication.preferences.isNotificationPermissionChecked() && MyApplication.isLogin) {
                checkAndRequestNotificationPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSTION_REQUEST_CODE) {
            if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                if (locationSource.isActivated) {
                    Log.d("MapFragment", "위치 권한 승인됨")

                    mixpanel.people.set("location_permission", "Granted")

                    moveToCurrentLocation() // 권한 승인 후 위치 이동
                } else {
                    Log.e("MapFragment", "위치 권한 거부됨")

                    mixpanel.people.set("location_permission", "Denied")
                }

                if (!MyApplication.preferences.isNotificationPermissionChecked() && MyApplication.isLogin) {
                    checkAndRequestNotificationPermission()
                }
            }
        }
        else if (requestCode == NOTIFICATION_PERMISSTION_REQUEST_CODE) {
            val allowed = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

            if (allowed) {
                Log.d("MapFragment", "🔔 알림 권한 허용됨")

                mixpanel.people.set("notification_permission", "Granted")
            } else {
                Log.d("MapFragment", "🔕 알림 권한 거부됨")

                mixpanel.people.set("notification_permission", "Denied")
            }

            sendFcmToken(allowed)
        }
    }

    fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSTION_REQUEST_CODE
                )
            }
        } else {
            // Android 12 이하는 무조건 true
            sendFcmToken(allowed = true)
        }
    }

    fun checkPermissionsAndSendToMixpanel(context: Context) {
        // ✅ 알림 권한 체크
        val isNotificationAllowed = NotificationManagerCompat.from(context).areNotificationsEnabled()
        mixpanel.people.set("notification_permission", if (isNotificationAllowed) "Granted" else "Denied")

        // ✅ 위치 권한 체크
        val isLocationAllowed = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        mixpanel.people.set("location_permission", if (isLocationAllowed) "Granted" else "Denied")
    }

    fun sendFcmToken(allowed: Boolean) {
        MyApplication.preferences.setNotificationPermissionChecked(true)

        val token = MyApplication.preferences.getFCMToken().toString()
        val body = FcmTokenRequest(
            InfoManager(mainActivity).getUserId()?.toInt() ?: 0,
            allowed,
            token,
            "ANDROID"
        )
        loginViewModel.saveFcmToken(mainActivity, body)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(map: NaverMap) {
        Log.d("##", "onMapReady")
        naverMap = map

        naverMap.mapType = NaverMap.MapType.Navi // 네비게이션 스타일 (다크 테마 적용됨)

        // 지도 옵션 설정
        naverMap.run {
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            isIndoorEnabled = true
            isNightModeEnabled = true
            uiSettings.run {
                setLogoMargin(40, 0, 40, 320)
                isScaleBarEnabled = false
                isZoomControlEnabled = false
                isCompassEnabled = false
            }
        }

        // 위치 소스 연결
        naverMap.locationSource = locationSource

        // 현재 위치 가져오기 & 초기 지도 설정
        val lastLocation = locationSource.lastLocation

        if (lastLocation != null) {
            // 위치 정보가 있을 경우, 현재 위치로 지도 초기화
            val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(currentLatLng, 15.0).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        } else {
            // 위치 정보가 없을 경우, 추적 모드 활성화 (현재 위치 자동 업데이트)
            checkLocationPermission()
        }

        // 지도 화면이 로딩된 후, 현재 보이는 지도를 기준으로 매장 리스트 가져오기
        fetchStoresBasedOnMapView()

        // 확대/이동이 발생하면 다시 매장 데이터 로드
        naverMap.addOnCameraIdleListener {
            val currentCenter = naverMap.cameraPosition.target

            // 위치 변경 없으면 리턴
            if (lastCameraPosition != null && lastCameraPosition == currentCenter) return@addOnCameraIdleListener

            lastCameraPosition = currentCenter
            fetchStoresBasedOnMapView()
        }

        // 확대 축소 범위 설정
        naverMap.maxZoom = 20.0
        naverMap.minZoom = 10.0

        // 위치 추적 모드 설정
        naverMap.locationTrackingMode = LocationTrackingMode.None
    }

    fun observeViewModel() {
        viewModel.run {
            storeInfo.observe(viewLifecycleOwner) {
                getStoreInfo = it

                markers.clear()

                for (i in 0 until (getStoreInfo.size ?: 0)) {
                    val marker = Marker()
                    var latitude = getStoreInfo[i].latitude?.toDouble()
                    var longitude = getStoreInfo[i].longitude?.toDouble()
                    marker.position = LatLng(latitude!!, longitude!!)
                    if(getStoreInfo[i].isAvailable == true) {
                        marker.icon = OverlayImage.fromResource(R.drawable.ic_marker_enabled)
                    } else {
                        marker.icon = OverlayImage.fromResource(R.drawable.ic_marker_disabled)
                    }
                    markers.add(marker)
                }


                for (m in 0 until markers.size) {

                    var makerIndex = m
                    markers[makerIndex].map = naverMap

                    // 마커 클릭한 경우
                    markers[makerIndex].setOnClickListener {
                        mixpanel.track("click_home_map_pin", null)

                        // 마커 변경
                        binding.bottomSheetStoreList.layoutStoreList.visibility = View.VISIBLE
                        mainActivity.run {
                            hideBottomNavigation(true)
                            hideMyLocationButton(true)
                            hideMapButton(true)
                        }

                        binding.bottomSheetStoreList.run {
                            val storeInfo = getStoreInfo[makerIndex]

                            if(storeInfo.storeMainImageUrl.isNullOrEmpty()) {
                                imageViewStore.setImageResource(R.drawable.img_store_main_basic)
                            } else {
                                Glide.with(mainActivity).load(storeInfo.storeMainImageUrl)
                                    .into(imageViewStore)
                            }
                            textViewStoreIsOpen.text = storeInfo.isOpen
                            textViewStoreCloseOrOpenTime.text = storeInfo.openingInfo
                            textViewStoreName.text = storeInfo.storeName
                            textViewStoreCall.text = storeInfo.storeTel
                            textViewStoreAvailableDrink.text = storeInfo.availableDrinks?.joinToString(",")

                            if(storeInfo.isAvailable == true) {
                                layoutStoreUnavailable.visibility = View.INVISIBLE
                            } else {
                                layoutStoreUnavailable.visibility = View.VISIBLE
                            }

                            layoutStoreList.setOnClickListener {
                                mixpanel.track("move_map_to_detail", null)

                                // 제휴업체 - 세부 화면으로 전환
                                var nextFragment = StoreDetailFragment()

                                val bundle = Bundle().apply { putLong("storeId", storeInfo.id) }

                                // 전달할 Fragment 생성
                                nextFragment = StoreDetailFragment().apply {
                                    arguments = bundle
                                }
                                mainActivity.supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView_main, nextFragment)
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }

                        // 클릭한 마커의 위치로 카메라 이동
                        val cameraUpdate = CameraUpdate.scrollTo((LatLng(markers[makerIndex].position.latitude, markers[makerIndex].position.longitude))).animate(
                            CameraAnimation.Easing
                        )
                        naverMap.moveCamera(cameraUpdate)

                        true
                    }

                    // 지도 클릭한 경우
                    naverMap.setOnMapClickListener { pointF, latLng ->
                        for (i in 0 until markers.size) {
                            if(getStoreInfo[i].isAvailable == true) {
                                markers[i].icon =
                                    OverlayImage.fromResource(R.drawable.ic_marker_enabled)
                            } else {
                                markers[i].icon =
                                    OverlayImage.fromResource(R.drawable.ic_marker_disabled)
                            }
                        }
                        binding.bottomSheetStoreList.layoutStoreList.visibility = View.GONE
                        mainActivity.run {
                            hideBottomNavigation(false)
                            hideMyLocationButton(false)
                            hideMapButton(false)
                        }
                    }
                }
            }
        }
    }


    private fun fetchStoresBasedOnMapView() {
        if (!this::naverMap.isInitialized) return // 지도 초기화 확인

        // 1️⃣ 현재 지도 중심 좌표 가져오기
        val centerLatLng = naverMap.cameraPosition.target
        val latitude = centerLatLng.latitude
        val longitude = centerLatLng.longitude

        // 2️⃣ 현재 지도 화면의 경계(LatLngBounds) 가져오기
        val bounds = naverMap.contentBounds

        // 3️⃣ 화면 상단의 위도(Latitude) 가져오기 (북쪽 위 경계)
        val northLat = bounds.northLatitude

        // 4️⃣ 반경(Radius) 계산 (중심 좌표 ↔ 북쪽 경계 거리)
        val radius = centerLatLng.distanceTo(LatLng(northLat, longitude))

        Log.d("DrinklyLog", "현재 지도 중심: lat=$latitude, lng=$longitude, 반경=$radius")

        MyApplication.latitude = latitude.toString()
        MyApplication.longitude = longitude.toString()
        MyApplication.radius = radius.toInt()

        // ✅ 현재 지도 중심 좌표 및 반경을 기반으로 매장 목록 요청
        viewModel.getStoreList(mainActivity, latitude.toString(), longitude.toString(), radius.toInt(), null)
    }
}