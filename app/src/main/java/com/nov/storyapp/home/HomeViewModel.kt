package com.nov.storyapp.home

import android.provider.ContactsContract.Data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nov.storyapp.data.model.DataModel
import com.nov.storyapp.data.repository.StoryRepository
import com.nov.storyapp.data.response.AllStoryResponse
import com.nov.storyapp.helper.ResultState
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: StoryRepository) :  ViewModel(){
    private val listStoryItem = MutableLiveData<ResultState<AllStoryResponse>>()
    val storyItem: LiveData<ResultState<AllStoryResponse>> = listStoryItem
    init {
        getStories()
    }

    private fun getStories() {
        viewModelScope.launch {
            val allstoryResponse = repository.getStories()
            allstoryResponse.asFlow().collect {
                listStoryItem.value = it
            }
        }
    }

    fun getSession(): LiveData<DataModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }


}