package com.example.paku.data.repository

import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.presence.ClockIn_InsideRequest
import com.example.paku.data.model.presence.ClockIn_InsideResponse
import com.example.paku.data.model.users.CP_Request
import com.example.paku.data.model.users.CP_Response
import com.example.paku.data.model.users.LoginRequest
import com.example.paku.data.model.users.LoginResponse
import com.example.paku.data.model.users.LogoutRequest
import com.example.paku.data.model.users.LogoutResponse
import com.example.paku.data.model.users.ProfileResponse
import com.example.paku.data.model.users.RegisterRequest
import com.example.paku.data.model.users.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body

class UserRepository {
    suspend fun register(id_pegawai: String, username: String, password: String, role: String, imei: String): Response<RegisterResponse> {
        return RetrofitClient.instance.register(RegisterRequest(id_pegawai, username, password, role, imei))
    }

    suspend fun login(username: String, password: String, imei: String): Response<LoginResponse> {
        return RetrofitClient.instance.login(LoginRequest(username, password, imei))
    }

    suspend fun getProfile(authHeader: String): Response<ProfileResponse> {
        return RetrofitClient.instance.getProfile(authHeader)
    }

    suspend fun validateToken(authHeader: String): Response<Void> {
        return RetrofitClient.instance.validateToken(authHeader)
    }

    suspend fun logout(refreshToken: String): Response<LogoutResponse> {
        return RetrofitClient.instance.logout(LogoutRequest(refreshToken))
    }

    suspend fun changePassword(
        authHeader: String,
        userId: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Response<CP_Response> {
        return RetrofitClient.instance.changePassword(authHeader, userId, CP_Request(currentPassword, newPassword, confirmPassword))
    }
}