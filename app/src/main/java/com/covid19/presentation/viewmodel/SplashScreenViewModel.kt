package com.covid19.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.RoomDatabase
import com.covid19.BuildConfig
import com.covid19.data.repository.CentersDatabase
import com.covid19.data.repository.RetrofitInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val retrofitInterface: RetrofitInterface = RetrofitInterface.create()
    private val roomDatabase: CentersDatabase = CentersDatabase.getInstance(context)!!

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    var isSaveComplete = false

    fun updateProgress() {
        progressCount()
        viewModelScope.launch {
            for (i in 1..10) {
                val result = retrofitInterface.getCenters(i, 10, BuildConfig.SERVICE_KEY)
                Log.d("test", "result: {${result.currentCount}\n {${result.data}")
                for (j in 0..9) {
                    roomDatabase.centersDao().insert(result.data[j])
                }

                //delay(200)
            }
            isSaveComplete = true
            Log.d("test", "saveComplete")
        }
    }

    private fun progressCount() {
        viewModelScope.launch {
            var delayed = false
            while (_progress.value < 100) {
                if (_progress.value < 80) {
                    delay(20)
                    _progress.value += 1
                }
                else if (!isSaveComplete && _progress.value == 80) {
                    delay(20)
                    delayed = true
                }
                else if (isSaveComplete && _progress.value >= 80 && !delayed) {
                    delay(20)
                    _progress.value += 1
                }
                else if (isSaveComplete && _progress.value >= 80 && delayed) {
                    delay(4)
                    _progress.value += 1
                }
            }
        }
    }
}