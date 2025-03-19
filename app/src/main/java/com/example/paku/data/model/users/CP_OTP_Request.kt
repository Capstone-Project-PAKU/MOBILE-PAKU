package com.example.paku.data.model.users

data class CP_OTP_Request(
    val email: String,
    val newPassword: String,
    val confirmPassword: String
)
