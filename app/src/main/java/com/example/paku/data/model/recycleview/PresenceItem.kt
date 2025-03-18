package com.example.paku.data.model.recycleview

data class PresenceItem(
    val tanggal_presensi: String,
    val waktu_masuk: String,
    val waktu_keluar: String,
    val lokasi_masuk: String,
    val lokasi_keluar: String,
    val validasi_masuk: String,
    val validasi_keluar: String
)
