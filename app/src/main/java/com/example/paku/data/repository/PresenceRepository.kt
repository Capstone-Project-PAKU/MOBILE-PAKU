package com.example.paku.data.repository

import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.presence.ClockIn_InsideRequest
import com.example.paku.data.model.presence.ClockIn_InsideResponse
import com.example.paku.data.model.presence.ClockOut_InsideRequest
import com.example.paku.data.model.presence.ClockOut_InsideResponse
import com.example.paku.data.model.presence.GetCurrentPresenceResponse
import com.example.paku.data.model.recycleview.PresenceItem
import retrofit2.Response

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

    suspend fun getCurrentPresence(
        authHeader: String,
    ): Response<GetCurrentPresenceResponse> {
        return RetrofitClient.instance.getCurrentPresence(authHeader)
    }
}