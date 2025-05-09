package com.example.paku.data.api

import android.content.Context
import com.example.paku.network.TokenInterceptor
import com.example.paku.utils.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:6969/"

    private lateinit var tokenManager: TokenManager
    private lateinit var apiService: ApiService

    fun init(context: Context) {
        tokenManager = TokenManager(context.applicationContext)

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(tokenManager))
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun getInstance() : ApiService {
        if (!::apiService.isInitialized) {
            throw IllegalStateException("RetrofitClient is not initialized. Call init(context) first.")
        }
        return apiService
    }

//    val instance: ApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }
}