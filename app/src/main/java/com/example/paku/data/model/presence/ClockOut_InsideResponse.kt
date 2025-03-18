package com.example.paku.data.model.presence

import com.example.paku.data.model.list.PresenceOutData

data class ClockOut_InsideResponse(
    val status: String,
    val message: String,
    val data: PresenceOutData
)
