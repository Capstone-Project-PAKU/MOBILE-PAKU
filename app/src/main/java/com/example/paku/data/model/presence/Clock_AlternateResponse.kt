package com.example.paku.data.model.presence

import com.example.paku.data.model.list.PresenceAltData

data class Clock_AlternateResponse(
    val status: String,
    val message: String,
    val data: PresenceAltData
)
