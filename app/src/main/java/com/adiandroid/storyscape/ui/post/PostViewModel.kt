package com.adiandroid.storyscape.ui.post

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adiandroid.storyscape.data.api.ApiConfig
import com.adiandroid.storyscape.data.datastore.UserPreference
import com.adiandroid.storyscape.data.model.response.PostStoriesResponse
import com.adiandroid.storyscape.utility.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PostViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun postStory(imageFile: File, desc: String, storyLatitude: Double, storyLongitude: Double) {
        _isLoading.value = true
        val file = reduceFileImage(imageFile)

        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        val token = UserPreference(context).getUser().token

        val client = token?.let {
            ApiConfig().getApiService().postStories(
                token = "Bearer $it",
                file = imageMultipart,
                description = description,
                lat = storyLatitude,
                lon = storyLongitude
            )
        }

        client?.enqueue(object : Callback<PostStoriesResponse> {
            override fun onResponse(
                call: Call<PostStoriesResponse>,
                response: Response<PostStoriesResponse>
            ) {
                if (response.body()?.error == false) {
                    _isLoading.value = false
                    _isError.value = false
                } else {
                    _isLoading.value = false
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<PostStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }

        })
    }

}