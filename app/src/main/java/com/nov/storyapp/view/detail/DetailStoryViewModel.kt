package com.nov.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.nov.storyapp.data.repository.StoryRepository
import com.nov.storyapp.data.response.DetailStoryResponse
import com.nov.storyapp.helper.ResultState
import kotlinx.coroutines.launch

class DetailStoryViewModel(private val repository: StoryRepository): ViewModel() {
    private val detailStory = MutableLiveData<ResultState<DetailStoryResponse>>()
    val story: LiveData<ResultState<DetailStoryResponse>> = detailStory

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            val storyResponse = repository.getDetailStories(id)
            storyResponse.asFlow().collect {
                detailStory.value = it
            }
        }
    }
}