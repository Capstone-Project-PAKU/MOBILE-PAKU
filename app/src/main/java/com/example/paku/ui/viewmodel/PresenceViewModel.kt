package com.example.paku.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paku.data.model.detail.DetailData
import com.example.paku.data.model.list.CurrentPresenceData
import com.example.paku.data.model.list.PresenceAltData
import com.example.paku.data.model.list.PresenceInData
import com.example.paku.data.model.list.PresenceOutData
import com.example.paku.data.model.list.PresenceOutsideData
import com.example.paku.data.repository.PresenceRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class PresenceViewModel: ViewModel() {
    private val repository = PresenceRepository()

    fun clockIn_Inside(
        id_user: String,
        tanggal_presensi: String,
        waktu_masuk: String,
        onResult: (Boolean, String, PresenceInData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.clockIn_Inside(id_user, tanggal_presensi, waktu_masuk)

                if (response.isSuccessful) {
                    val clockInInsideResponse = response.body()
                    onResult(true, clockInInsideResponse?.message ?: "Berhasil Persensi Masuk", clockInInsideResponse?.data)
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error, null)
                }
            } catch (e: HttpException) {
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

    fun clockOut_Inside(
        id_user: String,
        tanggal_presensi: String,
        waktu_keluar: String,
        onResult: (Boolean, String, PresenceOutData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.clockOut_Inside(id_user, tanggal_presensi, waktu_keluar)

                if (response.isSuccessful){
                    val clockOutInsideResponse = response.body()
                    onResult(true, clockOutInsideResponse?.message ?: "Berhasil Persensi Masuk", clockOutInsideResponse?.data)
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error, null)
                }
            } catch (e: HttpException) {
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

    fun getCurrentPresence(
        id_user: String,
        onResult: (Boolean, String?, CurrentPresenceData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getCurrentPresence(id_user)

                if (response.isSuccessful){
                    val currentPresenceResponse = response.body()
                    onResult(true, null, currentPresenceResponse?.data)
                }
            } catch (e: HttpException) {
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

    fun clockIn_Alternate(
        file_selfie: MultipartBody.Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_masuk: String,
        lokasi: String,
        onResult: (Boolean, String, PresenceAltData?) -> Unit
    ){
        viewModelScope.launch {
            try {
                val response = repository.clockIn_Alternate(file_selfie, id_user, tanggal_presensi, waktu_masuk, lokasi)

                if (response.isSuccessful) {
                    val clockIn = response.body()
                    onResult(true, clockIn?.message ?: "Berhasil melakukan presensi", clockIn?.data)
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error, null)
                }
            } catch (e: HttpException) {
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

    fun clockOut_Alternate(
        file_selfie: MultipartBody.Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_keluar: String,
        lokasi: String,
        onResult: (Boolean, String, PresenceAltData?) -> Unit
    ){
        viewModelScope.launch {
            try {
                val response = repository.clockOut_Alternate(file_selfie, id_user, tanggal_presensi, waktu_keluar, lokasi)

                if (response.isSuccessful) {
                    val clockOut = response.body()
                    onResult(true, clockOut?.message ?: "Berhasil melakukan presensi", clockOut?.data)
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error, null)
                }
            } catch (e: HttpException) {
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

    fun clockIn_Outside(
        file_selfie: MultipartBody.Part,
        file_dokumen: MultipartBody.Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_masuk: String,
        lokasi: String,
        onResult: (Boolean, String, PresenceOutsideData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.clockIn_Outside(file_selfie, file_dokumen, id_user, tanggal_presensi, waktu_masuk, lokasi)

                if (response.isSuccessful) {
                    val clockIn = response.body()
                    onResult(true, clockIn?.message ?: "Berhasil melakukan presensi", clockIn?.data)
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error, null)
                }
            } catch (e: HttpException) {
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

    fun clockOut_Outside(
        file_selfie: MultipartBody.Part,
        file_dokumen: MultipartBody.Part,
        id_user: String,
        tanggal_presensi: String,
        waktu_keluar: String,
        lokasi: String,
        onResult: (Boolean, String, PresenceOutsideData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.clockOut_Outside(file_selfie, file_dokumen, id_user, tanggal_presensi, waktu_keluar, lokasi)

                if (response.isSuccessful) {
                    val clockOut = response.body()
                    onResult(true, clockOut?.message ?: "Berhasil melakukan presensi", clockOut?.data)
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error, null)
                }
            } catch (e: HttpException) {
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

    fun getDetailInfo(
        onResult: (Boolean, DetailData?, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getDetailInfo()
                if (response.isSuccessful) {
                    onResult(true, response.body()?.data, null)
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, null, error)
                }
            } catch (e: HttpException) {
                Log.e("UserViewModel", "Server error: ${e.message}")
                onResult(false, null, "Server error. Mohon coba lagi nanti.")
            }
        }
    }

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            val json = JSONObject(errorBody ?: "")
            when (response.code()) {
                400 -> json.getString("message")
                409 -> json.getString("message")
                500 -> json.getString("message")
                else -> json.getString("message")
            }
        } catch (e: Exception) {
            "Unknown error occurred."
        }
    }
}