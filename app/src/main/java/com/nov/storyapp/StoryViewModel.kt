package com.nov.storyapp

import android.credentials.CredentialDescription
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.nov.storyapp.data.model.DataModel
import com.nov.storyapp.data.repository.StoryRepository
import com.nov.storyapp.data.response.UploadStoryResponse
import com.nov.storyapp.helper.ResultState
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun postStory(multipartBody: MultipartBody.Part,
                  description: RequestBody
    ): LiveData<ResultState<UploadStoryResponse>> = repository.postStory(multipartBody, description)

    fun getDataLogin(): LiveData<DataModel> {
        return repository.getSession().asLiveData()
    }
}