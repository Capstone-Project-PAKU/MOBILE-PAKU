package com.example.paku.data.repository

import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.otp.SendRequest
import com.example.paku.data.model.otp.SendResponse
import com.example.paku.data.model.otp.VerifyRequest
import com.example.paku.data.model.otp.VerifyResponse
import com.example.paku.data.model.users.CP_OTP_Request
import com.example.paku.data.model.users.CP_OTP_Response
import com.example.paku.data.model.users.CP_Request
import com.example.paku.data.model.users.CP_Response
import com.example.paku.data.model.users.LoginRequest
import com.example.paku.data.model.users.LoginResponse
import com.example.paku.data.model.users.LogoutRequest
import com.example.paku.data.model.users.LogoutResponse
import com.example.paku.data.model.users.ProfileResponse
import com.example.paku.data.model.users.RefreshRequest
import com.example.paku.data.model.users.RefreshResponse
import com.example.paku.data.model.users.RegisterRequest
import com.example.paku.data.model.users.RegisterResponse
import retrofit2.Response

class UserRepository {
    suspend fun register(
        id_pegawai: String,
        username: String,
        email: String,
        password: String,
        role: String,
        id_android: String
    ): Response<RegisterResponse> {
        return RetrofitClient.instance.register(RegisterRequest(id_pegawai, username, email, password, role, id_android))
    }

    suspend fun login(username: String, password: String, id_android: String): Response<LoginResponse> {
        return RetrofitClient.instance.login(LoginRequest(username, password, id_android))
    }

    suspend fun getProfile(authHeader: String): Response<ProfileResponse> {
        return RetrofitClient.instance.getProfile(authHeader)
    }

    suspend fun validateToken(authHeader: String): Response<Void> {
        return RetrofitClient.instance.validateToken(authHeader)
    }

    suspend fun refreshToken(authHeader: String): Response<RefreshResponse> {
        return RetrofitClient.instance.refresh(RefreshRequest(authHeader))
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

    suspend fun changePasswordWithOTP(
        email: String,
        newPassword: String,
        confirmPassword: String
    ): Response<CP_OTP_Response> {
        return RetrofitClient.instance.changePasswordWithOTP(CP_OTP_Request(email, newPassword, confirmPassword))
    }

    suspend fun sendOTP(
        email: String
    ): Response<SendResponse> {
        return RetrofitClient.instance.sendOTP(SendRequest(email))
    }

    suspend fun verifyOTP(
        email: String,
        otp: String
    ): Response<VerifyResponse> {
        return RetrofitClient.instance.verifyOTP(VerifyRequest(email, otp))
    }
}