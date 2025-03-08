package com.example.paku.data.model.users

import com.example.paku.data.model.list.LoginData

data class LoginResponse(
    val status: String,
    val message: String,
    val data: LoginData
)
