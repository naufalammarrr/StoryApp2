package com.ammar.ammarstoryapp2.injection

import android.content.Context
import android.util.Log
import com.ammar.ammarstoryapp2.R
import com.ammar.ammarstoryapp2.database.StoryDatabase
import com.ammar.ammarstoryapp2.repository.UserRepository
import com.ammar.ammarstoryapp2.retrofit.ApiConfig
import com.ammar.ammarstoryapp2.utils.UserPreference
import com.ammar.ammarstoryapp2.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        Log.d(context.getString(R.string.token_disimpan), user.token)
        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return UserRepository(apiService, pref, storyDatabase)
    }

}