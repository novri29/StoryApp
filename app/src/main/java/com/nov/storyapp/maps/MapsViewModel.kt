package com.nov.storyapp.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nov.storyapp.data.repository.StoryRepository
import com.nov.storyapp.data.response.ListStoryItem
import com.nov.storyapp.helper.ResultState

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getMapStories(): LiveData<ResultState<List<ListStoryItem>>> = repository.getStoriesWithLocation()
}
