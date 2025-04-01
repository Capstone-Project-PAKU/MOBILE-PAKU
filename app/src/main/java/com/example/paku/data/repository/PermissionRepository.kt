package com.example.paku.data.repository

import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.permission.LeaveResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class PermissionRepository {
    suspend fun addWorkLeave(
        authHeader: String,
        file_cuti: Part,
        id_user: String,
        jenis_cuti: String,
        tgl_awal_cuti: String,
        tgl_akhir_cuti: String,
        keterangan_cuti: String
    ): Response<LeaveResponse> {
        val userIdPart = id_user.toRequestBody("text/plain".toMediaType())
        val leaveType = jenis_cuti.toRequestBody("text/plain".toMediaType())
        val startLeave = tgl_awal_cuti.toRequestBody("text/plain".toMediaType())
        val endLeave = tgl_akhir_cuti.toRequestBody("text/plain".toMediaType())
        val leaveInformation = keterangan_cuti.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.instance.addWorkLeave(
            authHeader,
            file_cuti,
            userIdPart,
            leaveType,
            leaveInformation,
            startLeave,
            endLeave
        )
    }
}