package com.example.paku.data.api

import com.example.paku.data.model.detail.GetDetailInfoResponse
import com.example.paku.data.model.otp.SendRequest
import com.example.paku.data.model.otp.SendResponse
import com.example.paku.data.model.otp.VerifyRequest
import com.example.paku.data.model.otp.VerifyResponse
import com.example.paku.data.model.permission.GetUserLeaveResponse
import com.example.paku.data.model.permission.LeaveResponse
import com.example.paku.data.model.presence.Clock_AlternateResponse
import com.example.paku.data.model.presence.ClockIn_InsideRequest
import com.example.paku.data.model.presence.ClockIn_InsideResponse
import com.example.paku.data.model.presence.ClockIn_OutsideResponse
import com.example.paku.data.model.presence.ClockOut_InsideRequest
import com.example.paku.data.model.presence.ClockOut_InsideResponse
import com.example.paku.data.model.presence.GetCurrentPresenceResponse
import com.example.paku.data.model.presence.GetUserPresenceResponse
import com.example.paku.data.model.salary.GetUserPayrollResponse
import com.example.paku.data.model.shedule.GetUserScheduleDetail
import com.example.paku.data.model.shedule.GetUserWorkingRecap
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
import retrofit2.http.Query

interface ApiService {
    // Users
    @POST("api/user/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<LogoutResponse>

    @GET("api/auth/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @GET("api/auth/validate")
    suspend fun validateToken(): Response<Void>

    @PUT("api/user/change-password/{id}")
    suspend fun changePassword(
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
        @Body request: ClockIn_InsideRequest
    ): Response<ClockIn_InsideResponse>

    @POST("api/presence/clockout-inside")
    suspend fun clockOut_Inside(
        @Body request: ClockOut_InsideRequest
    ): Response<ClockOut_InsideResponse>

    @Multipart
    @POST("api/presence/clockin-alternate")
    suspend fun clockIn_Alternate(
        @Part foto_selfie: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody,
        @Part("tanggal_presensi") tanggalPresensi: RequestBody,
        @Part("waktu_masuk") waktuMasuk: RequestBody,
        @Part("lokasi") lokasi: RequestBody
    ): Response<Clock_AlternateResponse>

    @Multipart
    @POST("api/presence/clockout-alternate")
    suspend fun clockOut_Alternate(
        @Part foto_selfie: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody,
        @Part("tanggal_presensi") tanggalPresensi: RequestBody,
        @Part("waktu_keluar") waktuKeluar: RequestBody,
        @Part("lokasi") lokasi: RequestBody
    ): Response<Clock_AlternateResponse>

    @Multipart
    @POST("api/presence/clockin-outside")
    suspend fun clockIn_Outside(
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
        @Part foto_selfie: MultipartBody.Part,
        @Part foto_dokumen: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody,
        @Part("tanggal_presensi") tanggalPresensi: RequestBody,
        @Part("waktu_keluar") waktuKeluar: RequestBody,
        @Part("lokasi") lokasi: RequestBody
    ): Response<ClockIn_OutsideResponse>

    @GET("api/presence/current")
    suspend fun getCurrentPresence(): Response<GetCurrentPresenceResponse>

    @GET("api/presence/user/{id}")
    suspend fun getUserPresence(
        @Path("id") userId: String,
        @Query("month") monthFilter: String?,
        @Query("year") yearFilter: String?,
        @Query("limit") limitFilter: Int?
    ): Response<GetUserPresenceResponse>

    // Permission
    @Multipart
    @POST("api/permission/")
    suspend fun addWorkLeave(
        @Part file_cuti: MultipartBody.Part,
        @Part("id_user") userId: RequestBody,
        @Part("jenis_cuti") leaveType: RequestBody,
        @Part("keterangan_cuti") leaveInformation: RequestBody,
        @Part("tgl_awal_cuti") startDate: RequestBody,
        @Part("tgl_akhir_cuti") endDate: RequestBody
    ): Response<LeaveResponse>

    @GET("api/permission/user/{id}")
    suspend fun getUserLeave(
        @Path("id") userId: String
    ): Response<GetUserLeaveResponse>

    @GET("api/permission/user/date/{id}")
    suspend fun getUserLeaveByDate(
        @Path("id") userId: String,
        @Query("tgl_awal_cuti") tglAwalCuti: String
    ): Response<GetUserLeaveResponse>

    // Schedule
    @GET("api/schedule/detail/{id}")
    suspend fun getUserSchedule(
        @Path("id") userId: String,
        @Query("month") monthFilter: String?,
        @Query("year") yearFilter: String?,
        @Query("date") dateFilter: String?
    ): Response<GetUserScheduleDetail>

    // Working recap
    @GET("api/schedule/recap/{id}")
    suspend fun getUserWorkingRecap(
        @Path("id") userId: String,
        @Query("month") monthFilter: String?,
        @Query("year") yearFilter: String?
    ): Response<GetUserWorkingRecap>

    // Payroll
    @GET("api/salary/payroll/user/{id}")
    suspend fun getUserPayroll(
        @Path("id") userId: String,
        @Query("month") monthFilter: String?,
        @Query("year") yearFilter: String?
    ): Response<GetUserPayrollResponse>

    // OTP
    @POST("api/otp/send")
    suspend fun sendOTP(@Body request: SendRequest): Response<SendResponse>

    @POST("api/otp/verify")
    suspend fun verifyOTP(@Body request: VerifyRequest): Response<VerifyResponse>

    // details
    @GET("api/detail")
    suspend fun getDetailInfo(): Response<GetDetailInfoResponse>
}