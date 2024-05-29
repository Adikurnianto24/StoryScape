package com.adiandroid.storyscape.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adiandroid.storyscape.data.api.ApiService
import com.adiandroid.storyscape.data.model.response.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val apiService: ApiService) : ViewModel() {
    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val isError = MutableLiveData<Boolean>()
    val loginResult = MutableLiveData<AuthResponse>()

    fun userLogin(email: String, password: String) {
        isLoading.value = true
        isError.value = false

        val call = apiService.postLogin(email, password)
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    loginResult.value = response.body()
                    isSuccess.value = true
                } else {
                    isError.value = true
                }
                isLoading.value = false
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                isLoading.value = false
                isError.value = true
            }
        })
    }
}