package com.example.paku.data.model.users

data class LoginRequest(
    val username: String,
    val password: String,
    val id_android: String
)

