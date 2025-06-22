package com.example.paku.data.repository

import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.detail.GetDetailInfoResponse
import com.example.paku.data.model.presence.ClockIn_InsideResponse
import com.example.paku.data.model.presence.ClockIn_OutsideResponse
import com.example.paku.data.model.presence.ClockOut_InsideResponse
import com.example.paku.data.model.presence.Clock_AlternateResponse
import com.example.paku.data.model.presence.GetCurrentPresenceResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class PresenceRepository {
    suspend fun clockIn_Inside(
        file_selfie: Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_masuk: String
    ): Response<ClockIn_InsideResponse> {
        val idUserPart = id_user.toRequestBody("text/plain".toMediaType())
        val tanggalPresensiPart = tanggal_presensi.toRequestBody("text/plain".toMediaType())
        val waktuMasukPart = waktu_masuk.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.getInstance().clockIn_Inside(
            file_selfie,
            idUserPart,
            tanggalPresensiPart,
            waktuMasukPart,
        )
    }

    suspend fun clockOut_Inside(
        file_selfie: Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_keluar: String
    ): Response<ClockOut_InsideResponse> {
        val idUserPart = id_user.toRequestBody("text/plain".toMediaType())
        val tanggalPresensiPart = tanggal_presensi.toRequestBody("text/plain".toMediaType())
        val waktuKeluarPart = waktu_keluar.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.getInstance().clockOut_Inside(
            file_selfie,
            idUserPart,
            tanggalPresensiPart,
            waktuKeluarPart
        )
    }

    suspend fun clockIn_Alternate(
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

        return RetrofitClient.getInstance().clockIn_Alternate(
            file_selfie,
            idUserPart,
            tanggalPresensiPart,
            waktuMasukPart,
            lokasiPart
        )
    }

    suspend fun clockOut_Alternate(
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

        return RetrofitClient.getInstance().clockOut_Alternate(
            file_selfie,
            idUserPart,
            tanggalPresensiPart,
            waktu_keluarPart,
            lokasiPart
        )
    }

    suspend fun clockIn_Outside(
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

        return RetrofitClient.getInstance().clockIn_Outside(
            file_selfie,
            file_dokumen,
            idUserPart,
            tanggalPresensiPart,
            waktuMasukPart,
            lokasiPart
        )
    }

    suspend fun clockOut_Outside(
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

        return RetrofitClient.getInstance().clockOut_Outside(
            file_selfie,
            file_dokumen,
            idUserPart,
            tanggalPresensiPart,
            waktuKeluarPart,
            lokasiPart
        )
    }

    suspend fun getCurrentPresence(userId: String): Response<GetCurrentPresenceResponse> {
        return RetrofitClient.getInstance().getCurrentPresence(userId)
    }

    suspend fun getDetailInfo(): Response<GetDetailInfoResponse> {
        return RetrofitClient.getInstance().getDetailInfo()
    }
}