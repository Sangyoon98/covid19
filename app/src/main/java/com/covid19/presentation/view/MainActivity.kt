package com.covid19.presentation.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.covid19.R
import com.covid19.databinding.ActivityMainBinding
import com.covid19.presentation.viewmodel.MainActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Naver Map Fragment
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        // 현위치 버튼 클릭 리스너
        binding.floatingActionButton.setOnClickListener {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }

        // Bottom Sheet Behavior
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = STATE_COLLAPSED

        // Room DB 데이터 읽기
        viewModel.loadCentersList()
        viewModel.centersListFlow
            .onEach { result ->
                result.forEach { centers ->
                    // 마커 설정
                    val marker = Marker()
                    marker.position = LatLng(centers.lat.toDouble(), centers.lng.toDouble())

                    // centerType 종류별 마커 색상 설정
                    when (centers.centerType) {
                        "중앙/권역" -> marker.iconTintColor = Color.GREEN
                        "지역" -> marker.iconTintColor = Color.BLUE
                        else -> marker.iconTintColor = Color.YELLOW
                    }

                    marker.map = naverMap

                    // 마커 클릭 리스너
                    marker.setOnClickListener {
                        binding.address.text = centers.address
                        binding.centerName.text = centers.centerName
                        binding.facilityName.text = centers.facilityName
                        binding.phoneNumber.text = centers.phoneNumber
                        binding.updatedAt.text = centers.updatedAt

                        // 카메라 이동
                        val cameraUpdate = CameraUpdate
                            .scrollAndZoomTo(marker.position, 15.0)
                            .animate(CameraAnimation.Easing)
                        naverMap.moveCamera(cameraUpdate)

                        // Bottom Sheet Visible / Gone
                        if (bottomSheetBehavior.state == STATE_EXPANDED) {
                            bottomSheetBehavior.state = STATE_COLLAPSED
                        } else {
                            bottomSheetBehavior.state = STATE_EXPANDED
                        }

                        false
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    // Permission Check
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}