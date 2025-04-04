package com.example.paku.data.model.list

data class WorkingRecapData(
    val nama_pegawai: String,
    val bulan: String,
    val tahun: String,
    val total_jadwal: Int,
    val total_hadir: Int,
    val total_absen: Int,
    val total_sakit: Int,
    val total_libur: Int,
    val tanpa_keterangan: Int,
    val sisa_kuota_libur: Int
)
