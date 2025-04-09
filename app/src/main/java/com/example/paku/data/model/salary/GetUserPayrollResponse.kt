package com.example.paku.data.model.salary

import com.example.paku.data.model.list.PayrollData

data class GetUserPayrollResponse(
    val status: String,
    val data: List<PayrollData>
)