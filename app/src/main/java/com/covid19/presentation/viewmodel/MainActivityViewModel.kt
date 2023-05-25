package com.covid19.presentation.viewmodel

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid19.data.repository.CentersDatabase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val roomDatabase: CentersDatabase = CentersDatabase.getInstance(context)!!

    fun setMarker(naverMap: NaverMap) {
        viewModelScope.launch {
            val result = roomDatabase.centersDao().getAll()

            result.forEach {
                val marker = Marker()
                marker.position = LatLng(it.lat.toDouble(), it.lng.toDouble())
                if (it.centerType == "중앙/권역")
                    marker.iconTintColor = Color.BLUE
                else
                    marker.iconTintColor = Color.GREEN
                marker.map = naverMap
                Log.d("test", "$it")

                marker.setOnClickListener {
                    val cameraUpdate = CameraUpdate
                        .scrollAndZoomTo(marker.position, 15.0)
                        .animate(CameraAnimation.Easing)
                    naverMap.moveCamera(cameraUpdate)
                    Log.d("test", "marker cliked")
                    false
                }
            }
        }
    }
}