package com.example.paku.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paku.data.model.list.LoginData
import com.example.paku.data.model.list.ProfileData
import com.example.paku.data.model.list.RefreshData
import com.example.paku.data.model.list.RegisterData
import com.example.paku.data.model.users.LoginResponse
import com.example.paku.data.model.users.ProfileResponse
import com.example.paku.data.model.users.RefreshResponse
import com.example.paku.data.model.users.RegisterResponse
import com.example.paku.data.repository.UserRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UserViewModel: ViewModel() {
    private val respository = UserRepository()

    fun register(
        id_pegawai: String,
        username: String,
        email: String,
        password: String,
        role: String,
        id_android: String,
        onResult: (Boolean, String, RegisterData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response: Response<RegisterResponse> =
                    respository.register(id_pegawai, username, email, password, role, id_android)
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    val registerData = registerResponse?.data
                    onResult(true, response.body()?.message ?: "Registrasi Berhasil!", registerData)
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

    fun login(
        username: String,
        password: String,
        id_android: String,
        onResult: (Boolean, String?, LoginData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response: Response<LoginResponse> = respository.login(username, password, id_android)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val loginData = loginResponse?.data
                    onResult(true, loginResponse?.message, loginData)
                } else {
                    val error = parseErrorMessage(response) ?: "Invalid credentials"
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

    fun getProfile(
        token: String,
        onResult: (Boolean, ProfileData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response: Response<ProfileResponse> = respository.getProfile("Bearer $token")
                if (response.isSuccessful) {
                    onResult(true, response.body()?.data)
                } else {
                    onResult(false, null)
                }
            } catch (e: Exception){
                onResult(false, null)
            }
        }
    }

    fun validateToken(
        token: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = respository.validateToken(token)

                if (response.isSuccessful){
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun refresh(
        token: String,
        onResult: (Boolean, RefreshData?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = respository.refreshToken("Bearer $token")

                if (response.isSuccessful) {
                    val result = response.body()
                    onResult(true, result?.data)
                }
            }catch (e: Exception) {
                onResult(false, null)
            }
        }
    }

    fun logout(
        refreshToken: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = respository.logout(refreshToken)

                if (response.isSuccessful) {
                    onResult(true, response.body()?.message ?: "Logout Berhasil!")
                } else {
                    val error = parseErrorMessage(response) ?: "Invalid credentials"
                    onResult(false, error)
                }
            } catch (e: HttpException) {
                Log.e("UserViewModel", "Server error: ${e.message}")
                onResult(false, "Server error. Mohon coba lagi nanti.")
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error: ${e.message}")
                onResult(false, "Jaringan error. Mohon periksa koneksi internet anda.")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Unexpected error: ${e.localizedMessage}")
                onResult(false, "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    fun changePassword(
        token: String,
        userId: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = respository.changePassword("Bearer $token", userId, currentPassword, newPassword, confirmPassword)

                if (response.isSuccessful) {
                    onResult(true, response.body()?.message ?: "Berhasil mengganti password")
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error ?: "Gagal mengganti password")
                }
            } catch (e: HttpException) {
                Log.e("UserViewModel", "Server error: ${e.message}")
                onResult(false, "Server error. Mohon coba lagi nanti.")
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error: ${e.message}")
                onResult(false, "Jaringan error. Mohon periksa koneksi internet anda.")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Unexpected error: ${e.localizedMessage}")
                onResult(false, "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    fun changePasswordWithOTP(
        email: String,
        newPassword: String,
        confirmPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = respository.changePasswordWithOTP(email, newPassword, confirmPassword)
                if (response.isSuccessful) {
                    onResult(true, response.body()?.message ?: "Berhasil mengganti password")
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error)
                }
            } catch (e: HttpException) {
                Log.e("UserViewModel", "Server error: ${e.message}")
                onResult(false, "Server error. Mohon coba lagi nanti.")
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error: ${e.message}")
                onResult(false, "Jaringan error. Mohon periksa koneksi internet anda.")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Unexpected error: ${e.localizedMessage}")
                onResult(false, "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    fun sendOTP(
        email: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = respository.sendOTP(email)
                if (response.isSuccessful) {
                    onResult(true, response.body()?.message ?: "kode sudah terkirim")
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error)
                }
            } catch (e: HttpException) {
                Log.e("UserViewModel", "Server error: ${e.message}")
                onResult(false, "Server error. Mohon coba lagi nanti.")
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error: ${e.message}")
                onResult(false, "Jaringan error. Mohon periksa koneksi internet anda.")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Unexpected error: ${e.localizedMessage}")
                onResult(false, "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    fun verifyOTP(
        email: String,
        otp: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = respository.verifyOTP(email, otp)
                if (response.isSuccessful) {
                    onResult(true, response.body()?.message ?: "Kode OTP Terverifikasi")
                } else {
                    val error = parseErrorMessage(response)
                    onResult(false, error)
                }
            } catch (e: HttpException) {
                Log.e("UserViewModel", "Server error: ${e.message}")
                onResult(false, "Server error. Mohon coba lagi nanti.")
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error: ${e.message}")
                onResult(false, "Jaringan error. Mohon periksa koneksi internet anda.")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Unexpected error: ${e.localizedMessage}")
                onResult(false, "Unexpected error: ${e.localizedMessage}")
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