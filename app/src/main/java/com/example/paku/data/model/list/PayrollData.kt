package com.example.paku.data.model.list

data class PayrollData(
    val id_penghasilan: Int,
    val id_user: String,
    val tanggal: String,
    val gaji_pokok: Double,
    val tunjangan: Double,
    val jumlah_ketidakhadiran: Int,
    val jam_lembur: Int,
    val uang_lembur: Double,
    val total_gaji: Double,
    val status_pembayaran: String
)