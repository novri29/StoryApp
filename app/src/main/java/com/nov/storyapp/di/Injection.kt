package com.nov.storyapp.di

import android.content.Context
import com.nov.storyapp.helper.AuthPreference
import com.nov.storyapp.data.api.ApiConfig
import com.nov.storyapp.data.database.StoryDatabase
import com.nov.storyapp.data.repository.StoryRepository
import com.nov.storyapp.helper.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val dataBase = StoryDatabase.getDatabase(context)
        val pref = AuthPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(dataBase,apiService, pref)
    }
}