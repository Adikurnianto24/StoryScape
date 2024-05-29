package com.adiandroid.storyscape.data.api

import com.adiandroid.storyscape.data.model.response.AuthResponse
import com.adiandroid.storyscape.data.model.response.PostResponse
import com.adiandroid.storyscape.data.model.response.PostStoriesResponse
import com.adiandroid.storyscape.data.model.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("name") name: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): Call<PostResponse>

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<AuthResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun postStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double,
        @Part("lon") lon: Double,
    ): Call<PostStoriesResponse>

    @GET("stories")
    suspend fun getPagingStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): Response<StoriesResponse>

    companion object {
        private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}