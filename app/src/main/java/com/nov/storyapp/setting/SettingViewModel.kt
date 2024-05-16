package com.nov.storyapp.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nov.storyapp.data.model.DataModel
import com.nov.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: StoryRepository) :  ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getData(): LiveData<DataModel> {
        return repository.getSession().asLiveData()
    }
}