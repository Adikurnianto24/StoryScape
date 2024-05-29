package com.adiandroid.storyscape.utility

import android.content.Context
import com.adiandroid.storyscape.data.api.ApiConfig
import com.adiandroid.storyscape.data.datapaging.StoryRepository
import com.adiandroid.storyscape.data.datapaging.database.StoryDatabase

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig().getApiService()
        return StoryRepository(context, apiService, database)
    }
}