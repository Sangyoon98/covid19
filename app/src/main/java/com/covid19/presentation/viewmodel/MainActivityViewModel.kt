package com.covid19.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid19.data.model.Centers
import com.covid19.data.repository.CentersDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val roomDatabase: CentersDatabase = CentersDatabase.getInstance(context)!!

    private val _centersListFlow = MutableSharedFlow<List<Centers>>()
    val centersListFlow: SharedFlow<List<Centers>> = _centersListFlow

    // Room DB 데이터 읽기
    fun loadCentersList() {
        viewModelScope.launch {
            val result = roomDatabase.centersDao().getAll()
            _centersListFlow.emit(result)
        }
    }
}