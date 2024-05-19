package com.nov.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.data.repository.StoryRepository
import com.nov.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<ResultState<RegisterResponse>>()
    val registerResult: LiveData<ResultState<RegisterResponse>> get() = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _registerResult.value = ResultState.Loading
                val result = repository.register(name, email, password)
                _registerResult.postValue(result)
            } catch (e: Exception) {
                _registerResult.postValue(ResultState.Error("${e.message}"))
            }
        }
    }
}