package com.nov.storyapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nov.storyapp.data.model.DataModel
import com.nov.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: StoryRepository) :  ViewModel(){

}