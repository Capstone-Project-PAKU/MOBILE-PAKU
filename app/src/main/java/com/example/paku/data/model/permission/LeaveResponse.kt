package com.example.paku.data.model.permission

import com.example.paku.data.model.list.WorkLeaveData

data class LeaveResponse(
    val status: String,
    val message: String,
    val data: WorkLeaveData
)
