package com.example.paku.data.model.users

data class RegisterRequest(
    val id_pegawai: String,
    val username: String,
    val password: String,
    val role: String,
    val imei: String
)
