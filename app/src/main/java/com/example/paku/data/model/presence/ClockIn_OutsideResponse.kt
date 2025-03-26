package com.example.paku.data.model.presence

import com.example.paku.data.model.list.PresenceOutsideData

data class ClockIn_OutsideResponse(
    val status: String,
    val message: String,
    val data: PresenceOutsideData
)
