package com.example.paku.data.model.list

import java.util.Date

data class PresenceInData(
    val id_presensi_masuk: String,
    val id_user: String,
    val tanggal_presensi: Date,
    val waktu_masuk: String,
    val status_validasi: String,
    val jenis_presensi: String,
    val foto_selfie: String
)
