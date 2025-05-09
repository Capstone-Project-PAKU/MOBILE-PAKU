package com.example.paku.utils

import android.content.Context
import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.users.RefreshRequest

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences("credential_pref", Context.MODE_PRIVATE)

    fun getAccessToken(): String? = prefs.getString("accessToken", null)
    fun getRefreshToken(): String? = prefs.getString("refreshToken", null)

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString("accessToken", accessToken)
            .putString("refreshToken", refreshToken)
            .apply()
    }

    suspend fun refreshToken(): String? {
        val refreshToken = getRefreshToken()
        val response = refreshToken?.let {
            RetrofitClient.getInstance().refresh(RefreshRequest(refreshToken))
        }

        if (response?.isSuccessful!!) {
            val newAccessToken = response.body()?.data?.accessToken
            val newRefreshToken = response.body()?.data?.refreshToken

            if (newAccessToken != null && newRefreshToken != null) {
                saveTokens(newAccessToken, newRefreshToken)
                return newAccessToken
            }
        }
        return null
    }
}