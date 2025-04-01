package com.example.paku.data.model.permission

import com.example.paku.data.model.list.WorkLeaveData

data class GetUserLeaveResponse(
    val status: String,
    val data: List<WorkLeaveData>
)
