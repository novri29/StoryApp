package com.nov.storyapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nov.storyapp.data.model.DataModel
import com.nov.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository): ViewModel() {
    fun getLoginData(): LiveData<DataModel> {
        return repository.getSession().asLiveData()
    }

    suspend fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}