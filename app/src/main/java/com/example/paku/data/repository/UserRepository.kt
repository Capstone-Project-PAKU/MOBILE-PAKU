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
        return RetrofitClient.getInstance().register(RegisterRequest(id_pegawai, username, email, password, role, id_android))
    }

    suspend fun login(username: String, password: String, id_android: String): Response<LoginResponse> {
        return RetrofitClient.getInstance().login(LoginRequest(username, password, id_android))
    }

    suspend fun getProfile(): Response<ProfileResponse> {
        return RetrofitClient.getInstance().getProfile()
    }

    suspend fun validateToken(): Response<Void> {
        return RetrofitClient.getInstance().validateToken()
    }

    suspend fun refreshToken(authHeader: String): Response<RefreshResponse> {
        return RetrofitClient.getInstance().refresh(RefreshRequest(authHeader))
    }

    suspend fun logout(refreshToken: String): Response<LogoutResponse> {
        return RetrofitClient.getInstance().logout(LogoutRequest(refreshToken))
    }

    suspend fun changePassword(
        userId: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Response<CP_Response> {
        return RetrofitClient.getInstance().changePassword(userId, CP_Request(currentPassword, newPassword, confirmPassword))
    }

    suspend fun changePasswordWithOTP(
        email: String,
        newPassword: String,
        confirmPassword: String
    ): Response<CP_OTP_Response> {
        return RetrofitClient.getInstance().changePasswordWithOTP(CP_OTP_Request(email, newPassword, confirmPassword))
    }

    suspend fun sendOTP(
        email: String
    ): Response<SendResponse> {
        return RetrofitClient.getInstance().sendOTP(SendRequest(email))
    }

    suspend fun verifyOTP(
        email: String,
        otp: String
    ): Response<VerifyResponse> {
        return RetrofitClient.getInstance().verifyOTP(VerifyRequest(email, otp))
    }
}