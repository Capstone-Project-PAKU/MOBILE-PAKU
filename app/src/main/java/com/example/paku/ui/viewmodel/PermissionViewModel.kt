package com.example.paku.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paku.data.model.list.WorkLeaveData
import com.example.paku.data.repository.PermissionRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class PermissionViewModel: ViewModel() {
    private val repository = PermissionRepository()

    fun AddWorkLeave(
        token: String,
        file_cuti: MultipartBody.Part,
        id_user: String,
        jenis_cuti: String,
        tgl_awal_cuti: String,
        tgl_akhir_cuti: String,
        keterangan_cuti: String,
        onResult: (Boolean, String, WorkLeaveData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.addWorkLeave("Bearer $token", file_cuti, id_user, jenis_cuti, tgl_awal_cuti, tgl_akhir_cuti, keterangan_cuti)

                if (response.isSuccessful) {
                    val leave = response.body()
                    onResult(true, leave?.message ?: "Berhasil mengajukan cuti", leave?.data)
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error, null)
                }
            }catch (e: HttpException) {
                Log.e("UserViewModel", "Server error: ${e.message}")
                onResult(false, "Server error. Mohon coba lagi nanti.", null)
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error: ${e.message}")
                onResult(false, "Jaringan error. Mohon periksa koneksi internet anda.", null)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Unexpected error: ${e.localizedMessage}")
                onResult(false, "Unexpected error: ${e.localizedMessage}", null)
            }
        }
    }

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            val json = JSONObject(errorBody ?: "")
            when (response.code()) {
                400 -> json.getString("message")
                401 -> json.getString("message")
                403 -> json.getString("message")
                404 -> json.getString("message")
                409 -> json.getString("message")
                500 -> json.getString("message")
                else -> json.getString("message")
            }
        } catch (e: Exception) {
            "Unknown error occurred."
        }
    }
}