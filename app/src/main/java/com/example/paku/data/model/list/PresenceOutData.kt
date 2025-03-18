package com.example.paku.data.model.list

data class PresenceOutData(
    val id_presensi_keluar: String,
    val id_user: String,
    val tanggal_presensi: String,
    val waktu_keluar: String,
    val jenis_presensi: String,
    val status_validasi: String
)
