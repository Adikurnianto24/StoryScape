package com.adiandroid.storyscape.data.datapaging

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.adiandroid.storyscape.data.api.ApiService
import com.adiandroid.storyscape.data.datapaging.database.StoryDatabase
import com.adiandroid.storyscape.data.model.response.ListStoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoryRepository(private val context: Context, private val apiService: ApiService, private val storyDatabase: StoryDatabase) {

    fun getListStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(context, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun refreshStories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getPagingStories(token = "your_token", page = 1, size = 5, location = 1)
                if (response.isSuccessful) {
                    response.body()?.let { storyResponse ->
                        storyDatabase.storyDao().deleteAll()
                        storyDatabase.storyDao().insertStory(storyResponse.listStory)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}