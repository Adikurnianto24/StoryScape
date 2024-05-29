package com.adiandroid.storyscape.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.adiandroid.storyscape.data.api.ApiService
import com.adiandroid.storyscape.data.model.response.AuthResponse
import com.adiandroid.storyscape.data.model.response.LoginResult
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var call: Call<AuthResponse>

    @Mock
    private lateinit var observerIsSuccess: Observer<Boolean>

    @Mock
    private lateinit var observerIsLoading: Observer<Boolean>

    @Mock
    private lateinit var observerIsError: Observer<Boolean>

    @Mock
    private lateinit var observerLoginResult: Observer<AuthResponse>

    @Captor
    private lateinit var callbackCaptor: ArgumentCaptor<Callback<AuthResponse>>

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(apiService)
        loginViewModel.isSuccess.observeForever(observerIsSuccess)
        loginViewModel.isLoading.observeForever(observerIsLoading)
        loginViewModel.isError.observeForever(observerIsError)
        loginViewModel.loginResult.observeForever(observerLoginResult)
    }

    @Test
    fun `userLogin success`() {
        val email = "adikurnianto66@gmail.com"
        val password = "Adi123456"

        val loginResult = LoginResult("Test User", "userId123", "token123")
        val authResponse = AuthResponse(loginResult, false, "Login successful")

        `when`(apiService.postLogin(email, password)).thenReturn(call)

        loginViewModel.userLogin(email, password)

        verify(call).enqueue(callbackCaptor.capture())
        callbackCaptor.value.onResponse(call, Response.success(authResponse))

        verify(observerIsLoading).onChanged(true)
        verify(observerIsError).onChanged(false)
        verify(observerLoginResult).onChanged(authResponse)
        verify(observerIsLoading).onChanged(false)
    }

    @Test
    fun `userLogin failure`() {
        val email = "test@example.com"
        val password = "password"

        `when`(apiService.postLogin(email, password)).thenReturn(call)

        loginViewModel.userLogin(email, password)

        verify(call).enqueue(callbackCaptor.capture())
        callbackCaptor.value.onFailure(call, Throwable("Network error"))

        verify(observerIsLoading).onChanged(true)
        verify(observerIsError).onChanged(false)
        verify(observerIsError).onChanged(true)
        verify(observerIsLoading).onChanged(false)
    }
}
