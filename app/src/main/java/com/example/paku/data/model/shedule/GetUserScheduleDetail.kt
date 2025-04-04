package com.example.paku.data.model.shedule

import com.example.paku.data.model.list.ScheduleDetailData

data class GetUserScheduleDetail(
    val status: String,
    val data: List<ScheduleDetailData>
)
