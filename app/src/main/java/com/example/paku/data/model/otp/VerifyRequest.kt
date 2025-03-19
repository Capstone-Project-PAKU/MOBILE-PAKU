package com.example.paku.data.model.otp

data class VerifyRequest(
    val email: String,
    val otp: String
)
