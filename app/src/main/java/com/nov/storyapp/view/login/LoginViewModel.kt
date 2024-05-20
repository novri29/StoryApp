package com.nov.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.data.repository.StoryRepository
import com.nov.storyapp.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> get() = _loginResult

    fun login(email:String, password:String){
        _loginResult.value = ResultState.Loading

        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginResult.value = result
        }
    }
}