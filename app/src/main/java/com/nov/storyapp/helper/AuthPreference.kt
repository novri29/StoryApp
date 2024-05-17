package com.nov.storyapp.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nov.storyapp.data.model.DataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthPreference private constructor(private val dataStore: DataStore<Preferences>){
    suspend fun saveSession(user: DataModel) {
        dataStore.edit {
            it[NAME] = user.name
            it[TOKEN] = user.token
            it[IS_LOGIN] = true
        }
    }

    fun getSession(): Flow<DataModel> {
        return dataStore.data.map {
            DataModel(
                it[NAME] ?: "",
                it[TOKEN] ?: "",
                it[IS_LOGIN] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreference? = null

        private val NAME = stringPreferencesKey("name")
        private val TOKEN = stringPreferencesKey("token")
        private val IS_LOGIN = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}