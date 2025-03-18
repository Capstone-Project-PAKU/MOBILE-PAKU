package com.example.paku.data.model.presence

data class ClockOut_InsideRequest(
    val id_user: String,
    val tanggal_presensi: String,
    val waktu_keluar: String
)
