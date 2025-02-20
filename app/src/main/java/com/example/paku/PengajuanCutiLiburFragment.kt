package com.example.paku

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.database.getStringOrNull
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class PengajuanCutiLiburFragment : Fragment() {

    private lateinit var tvPdfName: TextView
    private lateinit var btnUploadPdf: LinearLayout
    private lateinit var etTanggalMulai: EditText
    private lateinit var etTanggalSelesai: EditText
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val calendarMulai = Calendar.getInstance()
    private val calendarSelesai = Calendar.getInstance()

    // ActivityResultLauncher untuk memilih file PDF
    private lateinit var selectPdfLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_pengajuan_cuti_libur, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View
        tvPdfName = view.findViewById(R.id.tvPdfName)
        btnUploadPdf = view.findViewById(R.id.uploadPdf)
        etTanggalMulai = view.findViewById(R.id.TanggalMulai)
        etTanggalSelesai = view.findViewById(R.id.TanggalSelesai)

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener { parentFragmentManager.popBackStack() }

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }

        // Inisialisasi ActivityResultLauncher untuk memilih file PDF
        selectPdfLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val fileName = getFileName(uri)
                    tvPdfName.text = fileName ?: "Dokumen terpilih"
                }
            }
        }

        // Tombol untuk memilih file PDF
        btnUploadPdf.setOnClickListener { openFilePicker() }

        // Setup DatePicker untuk Tanggal Mulai
        setupTanggalMulai()

        // Setup DatePicker untuk Tanggal Selesai (tergantung Tanggal Mulai)
        setupTanggalSelesai()
    }

    // Fungsi untuk menampilkan DatePickerDialog saat EditText diklik
    private fun setupTanggalMulai() {
        etTanggalMulai.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendarMulai.set(year, month, dayOfMonth)
                    etTanggalMulai.setText(dateFormat.format(calendarMulai.time))

                    // Reset Tanggal Selesai jika sudah dipilih sebelumnya
                    etTanggalSelesai.text.clear()
                },
                calendarMulai.get(Calendar.YEAR),
                calendarMulai.get(Calendar.MONTH),
                calendarMulai.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() // Tidak bisa pilih sebelum hari ini
            datePicker.show()
        }
    }

    private fun setupTanggalSelesai() {
        etTanggalSelesai.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendarSelesai.set(year, month, dayOfMonth)
                    etTanggalSelesai.setText(dateFormat.format(calendarSelesai.time))
                },
                calendarSelesai.get(Calendar.YEAR),
                calendarSelesai.get(Calendar.MONTH),
                calendarSelesai.get(Calendar.DAY_OF_MONTH)
            )
            // Pastikan tanggal selesai minimal sama dengan tanggal mulai
            if (etTanggalMulai.text.isNotEmpty()) {
                datePicker.datePicker.minDate = calendarMulai.timeInMillis
            } else {
                datePicker.datePicker.minDate = System.currentTimeMillis()
            }
            datePicker.show()
        }
    }

    // Fungsi untuk memilih file PDF
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        selectPdfLauncher.launch(Intent.createChooser(intent, "Pilih file PDF"))
    }

    // Fungsi untuk mendapatkan nama file dari URI
    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                fileName = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }
}
