package com.adiandroid.storyscape.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adiandroid.storyscape.data.api.ApiConfig
import com.adiandroid.storyscape.data.model.response.PostResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel(){

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    private val _registerResult = MutableLiveData<PostResponse>()
    val registerResult : LiveData<PostResponse> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun userRegister(name: String, password: String, email: String) {
        _isLoading.value = true
        _isSuccess.value = false
        _isError.value = false

        val service = ApiConfig().getApiService().postRegister(name, password, email)
        service.enqueue(object : Callback<PostResponse>{
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                _isLoading.value = false
                if (response.body()?.error == false) {
                    _registerResult.value = response.body()
                    _isSuccess.value = true
                    _isError.value = false
                } else {
                    _isError.value = true
                    _isLoading.value = false
                    _isSuccess.value = false
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }

        })
    }
}