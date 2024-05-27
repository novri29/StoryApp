package com.nov.storyapp.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nov.storyapp.data.model.DataModel
import com.nov.storyapp.data.repository.StoryRepository
import com.nov.storyapp.data.response.ListStoryItem

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStoryPagingData(): LiveData<PagingData<ListStoryItem>> {
        return repository.getStories().cachedIn(viewModelScope)
    }

    fun getSession(): LiveData<DataModel> {
        return repository.getSession().asLiveData()
    }
}
