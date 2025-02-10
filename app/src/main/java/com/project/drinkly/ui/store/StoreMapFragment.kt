package com.project.drinkly.ui.store

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
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
import com.project.drinkly.databinding.FragmentHomeMapBinding
import com.project.drinkly.ui.MainActivity
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignStart

class StoreMapFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentHomeMapBinding
    lateinit var mainActivity: MainActivity

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap


    private val CURRENT_LOCATION_CODE = 200
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private lateinit var locationSource: FusedLocationSource // 위치를 반환하는 구현체

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeMapBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.run {
            NaverMapSdk.getInstance(mainActivity).client =
                NaverMapSdk.NaverCloudPlatformClient("${com.project.drinkly.BuildConfig.MAP_API_KEY}")

            // 버튼 클릭 시 현재 위치로 이동
            mainActivity.binding.buttonMyLocation.setOnClickListener {
                checkLocationPermission()
            }

            locationSource = FusedLocationSource(this@StoreMapFragment, LOCATION_PERMISSTION_REQUEST_CODE)
        }

        mainActivity.binding.buttonList.run {
            setImageResource(R.drawable.ic_list)
            setOnClickListener {
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
        mainActivity.run {
            hideBottomNavigation(false)
            hideMyLocationButton(false)
            hideMapButton(false)
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
            .setBackgroundDrawableResource(R.drawable.background_tooltip)
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
                12.0 // 줌 레벨
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
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSTION_REQUEST_CODE) {
            if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                if (locationSource.isActivated) {
                    Log.d("MapFragment", "위치 권한 승인됨")
                    moveToCurrentLocation() // 권한 승인 후 위치 이동
                } else {
                    Log.e("MapFragment", "위치 권한 거부됨")
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
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
                setLogoMargin(40, 0, 40, 300)
                isScaleBarEnabled = false
                isZoomControlEnabled = false
            }
        }

        // 위치 소스 연결
        naverMap.locationSource = locationSource

        // 현재 위치 가져오기 & 초기 지도 설정
        val lastLocation = locationSource.lastLocation
        if (lastLocation != null) {
            // 위치 정보가 있을 경우, 현재 위치로 지도 초기화
            val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(currentLatLng, 12.0).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        } else {
            // 위치 정보가 없을 경우, 추적 모드 활성화 (현재 위치 자동 업데이트)
            checkLocationPermission()
//            naverMap.locationTrackingMode = LocationTrackingMode.Follow
//            Log.d("MapFragment", "현재 위치 정보를 가져올 수 없어 추적 모드 활성화")
        }

        // 확대 축소 범위 설정
        naverMap.maxZoom = 20.0
        naverMap.minZoom = 10.0

        // 마커 위치 설정
        val latitude = 38.23143
        val longitude = 127.342
        val marker = Marker()

        marker.position = LatLng(latitude, longitude)
        marker.icon = OverlayImage.fromResource(R.drawable.ic_marker_enabled)
        marker.map = naverMap

        // 위치 추적 모드 설정
        naverMap.locationTrackingMode = LocationTrackingMode.None
    }
}