package com.example.paku.data.model.list

data class WorkLeaveData(
    val id_cuti: String,
    val id_user: String,
    val jenis_cuti: String,
    val tgl_awal_cuti: String,
    val tgl_akhir_cuti: String,
    val file_cuti: String,
    val status_validasi: String,
    val keterangan_cuti: String
)
