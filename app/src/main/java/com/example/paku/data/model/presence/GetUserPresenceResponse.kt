package com.example.paku.data.model.presence

import com.example.paku.data.model.recycleview.PresenceItem

data class GetUserPresenceResponse(
    val status: String,
    val page: Int,
    val data: List<PresenceItem>
)
