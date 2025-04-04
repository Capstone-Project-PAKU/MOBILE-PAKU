package com.example.paku.data.model.shedule

import com.example.paku.data.model.list.WorkingRecapData

data class GetUserWorkingRecap(
    val status: String,
    val data: List<WorkingRecapData>
)
