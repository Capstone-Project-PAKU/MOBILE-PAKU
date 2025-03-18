package com.example.paku.data.api

import com.example.paku.data.model.presence.ClockIn_InsideRequest
import com.example.paku.data.model.presence.ClockIn_InsideResponse
import com.example.paku.data.model.presence.ClockOut_InsideRequest
import com.example.paku.data.model.presence.ClockOut_InsideResponse
import com.example.paku.data.model.presence.GetCurrentPresenceResponse
import com.example.paku.data.model.presence.GetUserPresenceResponse
import com.example.paku.data.model.recycleview.PresenceItem
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
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    // Users
    @POST("api/user/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<LogoutResponse>

    @GET("api/auth/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @GET("api/auth/validate")
    suspend fun validateToken(
        @Header("Authorization") token: String
    ): Response<Void>

    @PUT("api/user/change-password/{id}")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Body request: CP_Request
    ): Response<CP_Response>

    @PUT("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<RefreshResponse>

    // presence
    @POST("api/presence/clockin-inside")
    suspend fun clockIn_Inside(
        @Header("Authorization") token: String,
        @Body request: ClockIn_InsideRequest
    ): Response<ClockIn_InsideResponse>

    @POST("api/presence/clockout-inside")
    suspend fun clockOut_Inside(
        @Header("Authorization") token: String,
        @Body request: ClockOut_InsideRequest
    ): Response<ClockOut_InsideResponse>

    @GET("api/presence/current")
    suspend fun getCurrentPresence(
        @Header("Authorization") token: String
    ): Response<GetCurrentPresenceResponse>

    @GET("api/presence/user/{id}")
    suspend fun getUserPresence(
        @Header("Authorization") token: String,
        @Path("id") userId: String
    ): Response<GetUserPresenceResponse>
}