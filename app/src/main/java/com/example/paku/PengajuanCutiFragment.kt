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
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.database.getStringOrNull
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class PengajuanCutiFragment : Fragment() {

    private lateinit var tvPdfName: TextView
    private lateinit var btnUploadPdf: LinearLayout
    private lateinit var etTanggalMulai: EditText
    private lateinit var etTanggalSelesai: EditText
    private lateinit var editTextJenisIzin: AutoCompleteTextView
    private lateinit var jenisIzinLayout: TextInputLayout

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val calendarMulai = Calendar.getInstance()
    private val calendarSelesai = Calendar.getInstance()

    private lateinit var selectPdfLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_pengajuan_cuti, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPdfName = view.findViewById(R.id.tvPdfName)
        btnUploadPdf = view.findViewById(R.id.uploadPdf)
        etTanggalMulai = view.findViewById(R.id.TanggalMulai)
        etTanggalSelesai = view.findViewById(R.id.TanggalSelesai)
        editTextJenisIzin = view.findViewById(R.id.JenisIzin)
        jenisIzinLayout = view.findViewById(R.id.textInputLayout) // Pastikan ID di XML sesuai

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener { parentFragmentManager.popBackStack() }

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }

        selectPdfLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val fileName = getFileName(uri)
                    tvPdfName.text = fileName ?: "Dokumen terpilih"
                }
            }
        }

        // Data dropdown (hanya 3 item)
        val options = listOf("Sakit (surat ada)", "Sakit (surat menyusul)", "Libur")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options)
        editTextJenisIzin.setAdapter(adapter)

        // Hapus hint saat memilih jenis izin
        editTextJenisIzin.setOnItemClickListener { _, _, position, _ ->
            jenisIzinLayout.hint = ""

            // Reset tanggal mulai & selesai hanya jika bukan "Sakit (surat menyusul)"
            if (options[position] != "Sakit (surat menyusul)") {
                etTanggalMulai.text.clear()
                etTanggalSelesai.text.clear()
            }
        }

        // Kembalikan hint jika input kosong
        editTextJenisIzin.addTextChangedListener {
            if (it.toString().isEmpty()) {
                jenisIzinLayout.hint = "Pilih jenis izin"
            }
        }

        btnUploadPdf.setOnClickListener { openFilePicker() }

        setupTanggalMulai()
        setupTanggalSelesai()
    }

    private fun setupTanggalMulai() {
        etTanggalMulai.setOnClickListener {
            val jenisIzin = editTextJenisIzin.text.toString()
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

            // Jika "Sakit (surat menyusul)", biarkan user memilih tanggal bebas
            if (jenisIzin != "Sakit (surat menyusul)") {
                datePicker.datePicker.minDate = System.currentTimeMillis()
            }

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

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        selectPdfLauncher.launch(Intent.createChooser(intent, "Pilih file PDF"))
    }

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