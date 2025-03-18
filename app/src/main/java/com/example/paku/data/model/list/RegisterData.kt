package com.example.paku.data.model.list

data class RegisterData(
    val id_user: String,
    val id_pegawai: String,
    val username: String,
    val hashedPassword: String,
    val role: String,
    val imei: String
)
