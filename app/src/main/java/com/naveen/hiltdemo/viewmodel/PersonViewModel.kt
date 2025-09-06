package com.naveen.hiltdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor() : ViewModel() {
    
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()
    
    private val _isCounting = MutableStateFlow(false)
    val isCounting: StateFlow<Boolean> = _isCounting.asStateFlow()
    
    private val _countComplete = MutableStateFlow(false)
    val countComplete: StateFlow<Boolean> = _countComplete.asStateFlow()
    
    fun startCounting() {
        if (_isCounting.value) return
        
        _isCounting.value = true
        _countComplete.value = false
        _counter.value = 0
        
        viewModelScope.launch {
            for (i in 1..10) {
                _counter.value = i
                delay(1000) // 1 second delay between increments
            }
            _isCounting.value = false
            _countComplete.value = true
        }
    }
    
    fun resetCounter() {
        _counter.value = 0
        _isCounting.value = false
        _countComplete.value = false
    }
}
