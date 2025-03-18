package com.example.paku.data.model.users

data class CP_Request(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)
