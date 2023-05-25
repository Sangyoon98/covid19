package com.covid19.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid19.BuildConfig
import com.covid19.data.repository.RetrofitInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor() : ViewModel() {

    private val retrofitInterface: RetrofitInterface = RetrofitInterface.create()

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    fun updateProgress() {
        viewModelScope.launch {
            for (i in 1..10) {
                val result = retrofitInterface.getCenters(i, 10, BuildConfig.SERVICE_KEY)
                Log.d("test", "result: {${result.currentCount}\n {${result.data}")
            }
            while (_progress.value < 100) {
                _progress.value += 1
                delay(20)
            }
        }
    }
}