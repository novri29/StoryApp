package com.nov.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.nov.storyapp.AuthPreference
import com.nov.storyapp.ResultState
import com.nov.storyapp.data.api.ApiConfig
import com.nov.storyapp.data.api.ApiService
import com.nov.storyapp.data.model.DataModel
import com.nov.storyapp.data.response.ErrorResponse
import com.nov.storyapp.data.response.LoginResponse
import com.nov.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

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

    suspend fun register(name: String, email: String, password: String): ResultState<RegisterResponse>{
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
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        if (email.isEmpty()) {
            return ResultState.Error("Harap Mengisi \"Email\" Terlebih Dahulu")
        }
        if (!email.matches(emailRegex)) {
            return ResultState.Error("\"Email\" Harus Berbentuk Email")
        }
        if (password.isEmpty()) {
            return ResultState.Error("Harap Mengisi \"Password\" Terlebih Dahulu")
        }
        if (password.length < 8) {
            return ResultState.Error("Make sure your password is at least 8 characters")
        }

        ResultState.Loading
        try {
            val response = apiService.login(email, password)
            if (response.error == true) {
                return ResultState.Error(response.message ?: "Unknown Error")
            } else {
                val loginResult = response.loginResult
                if (loginResult != null) {
                    val sesi = DataModel(
                        name = email,
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