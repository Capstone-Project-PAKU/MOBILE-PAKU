package com.example.paku.data.model.users

import com.example.paku.data.model.list.RegisterData

data class RegisterResponse(
    val message: String,
    val data: RegisterData
)
