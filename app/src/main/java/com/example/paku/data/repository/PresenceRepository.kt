package com.example.paku.data.repository

import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.presence.Clock_AlternateResponse
import com.example.paku.data.model.presence.ClockIn_InsideRequest
import com.example.paku.data.model.presence.ClockIn_InsideResponse
import com.example.paku.data.model.presence.ClockIn_OutsideResponse
import com.example.paku.data.model.presence.ClockOut_InsideRequest
import com.example.paku.data.model.presence.ClockOut_InsideResponse
import com.example.paku.data.model.presence.GetCurrentPresenceResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import kotlin.math.tan

class PresenceRepository {
    suspend fun clockIn_Inside(
        authHeader: String,
        id_user: String,
        tanggal_presensi: String,
        waktu_masuk: String
    ): Response<ClockIn_InsideResponse> {
        return RetrofitClient.instance.clockIn_Inside(authHeader, ClockIn_InsideRequest(id_user, tanggal_presensi, waktu_masuk))
    }

    suspend fun clockOut_Inside(
        authHeader: String,
        id_user: String,
        tanggal_presensi: String,
        waktu_keluar: String
    ): Response<ClockOut_InsideResponse> {
        return RetrofitClient.instance.clockOut_Inside(authHeader, ClockOut_InsideRequest(id_user, tanggal_presensi, waktu_keluar))
    }

    suspend fun clockIn_Alternate(
        authHeader: String,
        file_selfie: Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_masuk: String,
        lokasi: String
    ): Response<Clock_AlternateResponse> {
        val idUserPart = id_user.toRequestBody("text/plain".toMediaType())
        val tanggalPresensiPart = tanggal_presensi.toRequestBody("text/plain".toMediaType())
        val waktuMasukPart = waktu_masuk.toRequestBody("text/plain".toMediaType())
        val lokasiPart = lokasi.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.instance.clockIn_Alternate(
            authHeader,
            file_selfie,
            idUserPart,
            tanggalPresensiPart,
            waktuMasukPart,
            lokasiPart
        )
    }

    suspend fun clockOut_Alternate(
        authHeader: String,
        file_selfie: Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_keluar: String,
        lokasi: String
    ): Response<Clock_AlternateResponse> {
        val idUserPart = id_user.toRequestBody("text/plain".toMediaType())
        val tanggalPresensiPart = tanggal_presensi.toRequestBody("text/plain".toMediaType())
        val waktu_keluarPart = waktu_keluar.toRequestBody("text/plain".toMediaType())
        val lokasiPart = lokasi.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.instance.clockOut_Alternate(
            authHeader,
            file_selfie,
            idUserPart,
            tanggalPresensiPart,
            waktu_keluarPart,
            lokasiPart
        )
    }

    suspend fun clockIn_Outside(
        authHeader: String,
        file_selfie: Part,
        file_dokumen: Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_masuk: String,
        lokasi: String
    ): Response<ClockIn_OutsideResponse> {
        val idUserPart = id_user.toRequestBody("text/plain".toMediaType())
        val tanggalPresensiPart = tanggal_presensi.toRequestBody("text/plain".toMediaType())
        val waktuMasukPart = waktu_masuk.toRequestBody("text/plain".toMediaType())
        val lokasiPart = lokasi.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.instance.clockIn_Outside(
            authHeader,
            file_selfie,
            file_dokumen,
            idUserPart,
            tanggalPresensiPart,
            waktuMasukPart,
            lokasiPart
        )
    }

    suspend fun clockOut_Outside(
        authHeader: String,
        file_selfie: Part,
        file_dokumen: Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_keluar: String,
        lokasi: String
    ): Response<ClockIn_OutsideResponse> {
        val idUserPart = id_user.toRequestBody("text/plain".toMediaType())
        val tanggalPresensiPart = tanggal_presensi.toRequestBody("text/plain".toMediaType())
        val waktuKeluarPart = waktu_keluar.toRequestBody("text/plain".toMediaType())
        val lokasiPart = lokasi.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.instance.clockOut_Outside(
            authHeader,
            file_selfie,
            file_dokumen,
            idUserPart,
            tanggalPresensiPart,
            waktuKeluarPart,
            lokasiPart
        )
    }

    suspend fun getCurrentPresence(
        authHeader: String,
    ): Response<GetCurrentPresenceResponse> {
        return RetrofitClient.instance.getCurrentPresence(authHeader)
    }
}