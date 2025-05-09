package com.example.paku.network

import com.example.paku.utils.TokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val tokenManager: TokenManager
): Interceptor {
    private val tokenLock = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = tokenManager.getAccessToken()

        request = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = chain.proceed(request)

        if (response.code == 403) {
            response.close()

            return runBlocking {
                tokenLock.withLock {
                    val newAccessToken = tokenManager.refreshToken()
                    if (newAccessToken != null) {
                        val newRequest = request.newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer $newAccessToken")
                            .build()
                        return@runBlocking chain.proceed(newRequest)
                    } else {
                        return@runBlocking response
                    }
                }
            }
        }

        return response
    }
}