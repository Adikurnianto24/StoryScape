package com.adiandroid.storyscape.ui.home.maps

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adiandroid.storyscape.data.api.ApiConfig
import com.adiandroid.storyscape.data.datastore.UserPreference
import com.adiandroid.storyscape.data.model.response.ListStoryItem
import com.adiandroid.storyscape.data.model.response.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    init {
        getStories()
    }

    private fun getStories() {
        val token = UserPreference(context).getUser().token
        val client = token?.let {
            ApiConfig().getApiService().getStories(token = "Bearer $it", size = 100, location = 1)
        }
        client?.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listStories.value = responseBody.listStory
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        const val TAG = "mapsViewModel"
    }
}