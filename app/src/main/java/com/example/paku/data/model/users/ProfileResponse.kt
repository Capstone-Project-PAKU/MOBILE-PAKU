package com.example.paku.data.model.users

import com.example.paku.data.model.list.ProfileData

data class ProfileResponse(
    val status: String,
    val message: String,
    val data: ProfileData,
)
