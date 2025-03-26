package com.example.paku.data.api

import com.example.paku.data.model.otp.SendRequest
import com.example.paku.data.model.otp.SendResponse
import com.example.paku.data.model.otp.VerifyRequest
import com.example.paku.data.model.otp.VerifyResponse
import com.example.paku.data.model.presence.Clock_AlternateResponse
import com.example.paku.data.model.presence.ClockIn_InsideRequest
import com.example.paku.data.model.presence.ClockIn_InsideResponse
import com.example.paku.data.model.presence.ClockIn_OutsideResponse
import com.example.paku.data.model.presence.ClockOut_InsideRequest
import com.example.paku.data.model.presence.ClockOut_InsideResponse
import com.example.paku.data.model.presence.GetCurrentPresenceResponse
import com.example.paku.data.model.presence.GetUserPresenceResponse
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @PUT("api/user/change-password-otp")
    suspend fun changePasswordWithOTP(
        @Body request: CP_OTP_Request
    ): Response<CP_OTP_Response>

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

    @Multipart
    @POST("api/presence/clockin-alternate")
    suspend fun clockIn_Alternate(
        @Header("Authorization") token: String,
        @Part foto_selfie: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody,
        @Part("tanggal_presensi") tanggalPresensi: RequestBody,
        @Part("waktu_masuk") waktuMasuk: RequestBody
    ): Response<Clock_AlternateResponse>

    @Multipart
    @POST("api/presence/clockout-alternate")
    suspend fun clockOut_Alternate(
        @Header("Authorization") token: String,
        @Part foto_selfie: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody,
        @Part("tanggal_presensi") tanggalPresensi: RequestBody,
        @Part("waktu_keluar") waktuKeluar: RequestBody
    ): Response<Clock_AlternateResponse>

    @Multipart
    @POST("api/presence/clockin-outside")
    suspend fun clockIn_Outside(
        @Header("Authorization") token: String,
        @Part foto_selfie: MultipartBody.Part,
        @Part foto_dokumen: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody,
        @Part("tanggal_presensi") tanggalPresensi: RequestBody,
        @Part("waktu_masuk") waktuMasuk: RequestBody,
        @Part("lokasi") lokasi: RequestBody
    ): Response<ClockIn_OutsideResponse>

    @Multipart
    @POST("api/presence/clockout-outside")
    suspend fun clockOut_Outside(
        @Header("Authorization") token: String,
        @Part foto_selfie: MultipartBody.Part,
        @Part foto_dokumen: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody,
        @Part("tanggal_presensi") tanggalPresensi: RequestBody,
        @Part("waktu_keluar") waktuKeluar: RequestBody,
        @Part("lokasi") lokasi: RequestBody
    ): Response<ClockIn_OutsideResponse>

    @GET("api/presence/current")
    suspend fun getCurrentPresence(
        @Header("Authorization") token: String
    ): Response<GetCurrentPresenceResponse>

    @GET("api/presence/user/{id}")
    suspend fun getUserPresence(
        @Header("Authorization") token: String,
        @Path("id") userId: String
    ): Response<GetUserPresenceResponse>

    // OTP
    @POST("api/otp/send")
    suspend fun sendOTP(@Body request: SendRequest): Response<SendResponse>

    @POST("api/otp/verify")
    suspend fun verifyOTP(@Body request: VerifyRequest): Response<VerifyResponse>
}