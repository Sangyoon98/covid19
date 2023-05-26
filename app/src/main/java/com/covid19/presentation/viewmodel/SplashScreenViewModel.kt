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

    // Retrofit API 통신 데이터 Room DB 저장
    fun updateProgress() {
        progressCount()
        viewModelScope.launch {
            for (i in 1..10) {
                // Retrofit API 통신
                val result = retrofitInterface.getCenters(i, 10, BuildConfig.SERVICE_KEY)
                /** BuildConfig.SERVICE_KEY
                 *  local.properties 파일에 DECODING_SERVICE_KEY = "..."로 Decoding Key 보관해 사용
                **/
                Log.d("test", "result: {${result.currentCount}\n {${result.data}")

                // Room DB에 API 통신 데이터 저장
                for (j in 0..9) {
                    roomDatabase.centersDao().insert(result.data[j])
                }

                // API 데이터 저장이 완료되지 않을 경우 테스트를 위해 남겨진 코드
                // delay(300)
            }

            // 저장이 완료되면 isSaveComplete = true
            isSaveComplete = true
            Log.d("test", "saveComplete")
        }
    }

    // ProgressBar 로딩 로직
    private fun progressCount() {
        viewModelScope.launch {
            var delayed = false // Defalut Value : 딜레이 비활성화

            while (_progress.value < 100) { // ProgressBar 100%까지 진행
                if (_progress.value < 80) { // ProgressBar 80%까지 업데이트
                    delay(20)
                    _progress.value += 1
                }
                else if (!isSaveComplete && _progress.value == 80) {    // ProgressBar 80%이면서 저장이 완료되지 않았을 때
                    delay(20)
                    delayed = true  //딜레이 활성화
                }
                else if (isSaveComplete && _progress.value >= 80 && !delayed) { // 저장 완료 후 ProgressBar 80% 이상이면서 딜레이 비활성화시
                    delay(20)
                    _progress.value += 1
                }
                else if (isSaveComplete && _progress.value >= 80 && delayed) {  // 저장 완료 후 ProgressBar 80% 이상이면서 딜레이 활성화시
                    delay(4)
                    _progress.value += 1
                }
            }
        }
    }
}