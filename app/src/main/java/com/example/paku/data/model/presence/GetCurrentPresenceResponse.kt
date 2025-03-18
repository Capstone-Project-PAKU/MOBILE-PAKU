package com.example.paku.data.model.presence

import com.example.paku.data.model.list.CurrentPresenceData

data class GetCurrentPresenceResponse(
    val status: String,
    val data: CurrentPresenceData
)
