package com.example.paku.data.model.presence

import com.example.paku.data.model.list.PresenceInData

data class ClockIn_InsideResponse(
    val status: String,
    val message: String,
    val data: PresenceInData
)
