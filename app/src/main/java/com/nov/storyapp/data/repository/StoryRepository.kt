package com.nov.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nov.storyapp.helper.AuthPreference
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.data.api.ApiConfig
import com.nov.storyapp.data.api.ApiService
import com.nov.storyapp.data.model.DataModel
import com.nov.storyapp.data.response.AllStoryResponse
import com.nov.storyapp.data.response.ErrorResponse
import com.nov.storyapp.data.response.ListStoryItem
import com.nov.storyapp.data.response.LoginResponse
import com.nov.storyapp.data.response.RegisterResponse
import com.nov.storyapp.data.response.UploadStoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val authPreference: AuthPreference
){
    suspend fun saveSession(user: DataModel){
        authPreference.saveSession(user)
    }
    fun getSession(): Flow<DataModel> {
        return authPreference.getSession()
    }
    suspend fun logout(){
        authPreference.logout()
    }

    suspend fun register(name: String, email: String, password: String): ResultState<RegisterResponse> {
        ResultState.Loading
        return try{
            val response = apiService.register(name, email, password)
            if (response.error == true) {
                ResultState.Error(response.message ?: "Unknown Error")
            } else {
                ResultState.Success(response)
            }
        }catch (e: HttpException){
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            ResultState.Error(errorMessage.toString())
        }
    }

    suspend fun login(email: String, password: String): ResultState<LoginResponse> {

        ResultState.Loading
        try {
            val response = apiService.login(email, password)
            if (response.error == true) {
                return ResultState.Error(response.message ?: "Unknown Error")
            } else {
                val loginResult = response.loginResult
                if (loginResult != null) {
                    val sesi = DataModel(
                        name = loginResult.name ?: "",
                        token = loginResult.token ?: "",
                        isLogin = true
                    )
                    saveSession(sesi)
                    ApiConfig.token = loginResult.token ?: ""
                    return ResultState.Success(response)
                } else {
                    return ResultState.Error("Login is null")
                }
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            return ResultState.Error(errorMessage ?: "Unknown Error")
        }
    }

    fun getStories() = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.getStories()
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            ResultState.Error(errorMessage.toString())
        }
    }

    fun getDetailStories(id: String) = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.getDetailStory(id)
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultState.Error(errorMessage.toString()))
        } catch (e: IOException) {
            emit(ResultState.Error("Network error occurred. Please check your connection and try again."))
        } catch (e: JsonSyntaxException) {
            emit(ResultState.Error("Error parsing server response."))
        } catch (e: Exception) {
            emit(ResultState.Error("An unexpected error occurred: ${e.message}"))
        }
    }

    fun postStory(mutltipartBody: MultipartBody.Part,
                  description: RequestBody
    ): LiveData<ResultState<UploadStoryResponse>> = liveData {
                emit(ResultState.Loading)
        try {
            val client = apiService.postStory(mutltipartBody, description)
            if (client.error == false) {
                emit(ResultState.Success(client))
            } else {
                emit(ResultState.Error(client.message.toString()))
            }
        } catch (e: HttpException) {
            Log.e("PostStoryHTTP", "${e.message}")
            emit(ResultState.Error(e.message.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun postStoryGuest(
        multipartBody: MultipartBody.Part,
        description: RequestBody
    ): LiveData<ResultState<UploadStoryResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val client = apiService.postStoryGuest(multipartBody, description)
            if (client.error == false) {
                emit(ResultState.Success(client))
            } else {
                emit(ResultState.Error(client.message ?: "Unknown error"))
            }
        } catch (e: HttpException) {
            Log.e("PostStoryGuestHTTP", "${e.message}")
            emit(ResultState.Error(e.message.toString()))
        } catch (e: Exception) {
            Log.e("PostStoryGuest", "${e.message}")
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getStoriesWithLocation(): LiveData<ResultState<List<ListStoryItem>>> = liveData {
        emit(ResultState.Loading)
        try {
            val client = apiService.getStoriesWithLocation()
            val nonNullList = client.listStory?.filterNotNull() ?: emptyList()
            emit(ResultState.Success(nonNullList))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
            Log.e("GetMapStories", e.message.toString())
        }
    }


    companion object {
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            authPreference: AuthPreference
        ): StoryRepository =
            instance ?: synchronized(this){
                instance ?: StoryRepository(apiService, authPreference)
            }.also { instance = it }
    }
}