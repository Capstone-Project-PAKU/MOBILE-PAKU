package com.example.paku

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.paku.ui.popup.showFailedPopup
import com.example.paku.ui.popup.showPresenceSuccessPopup
import com.example.paku.ui.viewmodel.PermissionViewModel
import com.example.paku.ui.viewmodel.UserViewModel
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PengajuanCutiFragment : Fragment() {

    private lateinit var tvPdfName: TextView
    private lateinit var btnUploadPdf: LinearLayout
    private lateinit var etTanggalMulai: EditText
    private lateinit var etTanggalSelesai: EditText
    private lateinit var editTextJenisIzin: AutoCompleteTextView
    private lateinit var jenisIzinLayout: TextInputLayout
    private lateinit var etketeranganCuti: EditText
    private lateinit var btnSave: Button
    private lateinit var prefs: SharedPreferences
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var permissionHeader: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingOverlay: FrameLayout

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendarMulai = Calendar.getInstance()
    private val calendarSelesai = Calendar.getInstance()
    private var pdfFile: String? = null
    private var pdfName: String? = null
    private val permissionViewModel: PermissionViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

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
        jenisIzinLayout = view.findViewById(R.id.textInputLayout)
        btnSave = view.findViewById(R.id.btnSave)
        permissionHeader = view.findViewById(R.id.permissionHeader)
        etketeranganCuti = view.findViewById(R.id.etketeranganCuti)
        loadingOverlay = view.findViewById(R.id.loadingOverlay)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener { parentFragmentManager.popBackStack() }

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }

        selectPdfLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val res = getFilePathFromUri(uri)
                    pdfFile = res.first
                    pdfName = res.second
                    tvPdfName.text = pdfName ?: "Dokumen terpilih"
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

        btnSave.setOnClickListener {
            val jenis_cuti = editTextJenisIzin.text.toString()
            val tgl_awal_cuti = etTanggalMulai.text.toString()
            val tgl_akhir_cuti = etTanggalSelesai.text.toString()
            val keterangan_cuti = etketeranganCuti.text.toString()

            if (pdfFile == null) {
                Toast.makeText(requireContext(), "Surat Cuti tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val document = getPdfPart(pdfFile!!)

            if (
                jenis_cuti.isEmpty() ||
                tgl_akhir_cuti.isEmpty() ||
                tgl_awal_cuti.isEmpty() ||
                keterangan_cuti.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Semua data wajib di isi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading()

            addWorkLeave(document!!, userId, jenis_cuti, tgl_awal_cuti, tgl_akhir_cuti, keterangan_cuti, view)
        }

        fetchUserProfile()
        setupTanggalMulai()
        setupTanggalSelesai()
    }

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        loadingOverlay.visibility = View.VISIBLE
        btnSave.isEnabled = false
        btnUploadPdf.isEnabled = false
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        loadingOverlay.visibility = View.GONE
        btnSave.isEnabled = true
        btnUploadPdf.isEnabled = true
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

    private fun addWorkLeave(
        file_cuti: MultipartBody.Part,
        id_user: String,
        jenis_cuti: String,
        tgl_awal_cuti: String,
        tgl_akhir_cuti: String,
        keteranngan_cuti: String,
        view: View
    ) {
        permissionViewModel.AddWorkLeave(file_cuti, id_user, jenis_cuti, tgl_awal_cuti, tgl_akhir_cuti, keteranngan_cuti) { success, message, permissionData ->
            hideLoading()  // sembunyikan progress bar saat selesai

            if (success) {
                showPresenceSuccessPopup(view, message, this)
            } else {
                showFailedPopup(view, message)
            }
        }
    }

    private fun getPdfPart(filePath: String): MultipartBody.Part? {
        val file = File(filePath)

        if (!file.exists()) {
            println("Error: File does not exist!")
            return null
        }

        // Detect file type based on extension
        val mimeType = when {
            filePath.endsWith(".pdf", true) -> "application/pdf"
            else -> {
                println("Error: Invalid file type. Only PDF files are allowed!")
                return null
            }
        }

        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull()) // Convert file to RequestBody
        return MultipartBody.Part.createFormData("file_cuti", file.name, requestFile) // Set part name
    }

    private fun getFilePathFromUri(uri: Uri): Pair<String?, String?> {
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index != -1) {
                cursor.moveToFirst()
                val filename = cursor.getString(index)
                val file = File(requireContext().cacheDir, filename)

                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                return file.absolutePath to file.name
            }
        }
        return null to null
    }

    private fun fetchUserProfile() {
        userViewModel.getProfile() { success, userData ->
            if (success) {
                val userOccupation = userData?.jabatan?.let { capitalizeWords(it) }
                permissionHeader.text = userOccupation
            }
        }
    }

    private fun capitalizeWords(input: String): String {
        return input.split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercaseChar() } }
    }
}