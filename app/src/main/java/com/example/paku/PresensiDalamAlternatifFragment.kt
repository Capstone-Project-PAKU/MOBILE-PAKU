package com.example.paku

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.paku.ui.popup.showPresenceFailedPopup
import com.example.paku.ui.popup.showPresenceSuccessPopup
import com.example.paku.ui.viewmodel.PresenceViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PresensiDalamAlternatifFragment : Fragment() {
    private lateinit var cameraBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var photoPreview: ImageView
    private lateinit var imageUri: String
    private lateinit var prefs: SharedPreferences
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var validationStatus: TextView
    private lateinit var validationIcon: ImageView
    private val presenceViewModel: PresenceViewModel by viewModels()

    private val cameraResultLaucher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.getStringExtra("imagePath") ?: return@registerForActivityResult
            photoPreview.setImageURI(Uri.parse(imageUri))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_presensi_dalam_alternatif, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraBtn = view.findViewById(R.id.btnCamera)
        saveBtn = view.findViewById(R.id.btnClockAlt)
        photoPreview = view.findViewById(R.id.selfiePreview)
        validationIcon = view.findViewById(R.id.presenceAltValidationIcon)
        validationStatus = view.findViewById(R.id.presenceAltValidationStatus)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        getCurrentPresence(accessToken)

        cameraBtn.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            cameraResultLaucher.launch(intent)
        }

        saveBtn.setOnClickListener {
            val date = getCurrentDate()
            val photo = getFilePart(imageUri)
            val clockIn = prefs.getString("waktu_masuk_alt", null)
            if (clockIn == null) {
                val time = "07:10:00"
                clockInAlt(accessToken, photo!!, userId, date, time, view)
            } else {
                val time = "14:10:00"
                clockOutAlt(accessToken, photo!!, userId, date,time, view)
            }
        }

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener { parentFragmentManager.popBackStack() }

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun clockInAlt(
        token: String,
        photo: MultipartBody.Part,
        id_user: String,
        date: String,
        time: String,
        view: View
    ) {
        presenceViewModel.clockIn_Alternate(token, photo, id_user, date, time) { success, message, presenceData ->
            if (success) {
                prefs.edit().putString("waktu_masuk_alt", presenceData?.waktu_masuk).apply()
                showPresenceSuccessPopup(view, message)
                getCurrentPresence(token)
            } else {
                showPresenceFailedPopup(view, message, false)
            }
        }
    }

    private fun clockOutAlt(
        token: String,
        photo: MultipartBody.Part,
        id_user: String,
        date: String,
        time: String,
        view: View
    ) {
        presenceViewModel.clockOut_Alternate(token, photo, id_user, date, time) { success, message, presenceData ->
            if (success) {
                prefs.edit().remove("waktu_masuk_alt").apply()
                showPresenceSuccessPopup(view, message)
                getCurrentPresence(token)
            } else {
                showPresenceFailedPopup(view, message, false)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentPresence(token: String) {
        presenceViewModel.getCurrentPresence(token) { success, message, data ->
            if (success) {
                if (data == null) {
                    return@getCurrentPresence
                }
                if (data.validasi_masuk == "setuju" && data.waktu_keluar == "setuju") {
                    validationStatus.text = "Status Validasi: " + data.validasi_masuk
                    validationIcon.setImageResource(R.drawable.icon_accept)
                } else if (data.waktu_masuk == "ditolak" && data.waktu_keluar == "ditolak") {
                    validationStatus.text = "Status Validasi: " + data.validasi_masuk
                    validationIcon.setImageResource(R.drawable.icon_decline)
                } else {
                    validationStatus.text = "Status Validasi: pending"
                    validationIcon.setImageResource(R.drawable.icon_forbidden)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        return  formattedDate
    }

    fun getFilePart(filePath: String): MultipartBody.Part? {
        val file = File(filePath)

        if (!file.exists()) {
            println("Error: File does not exist!")
            return null
        }

        // Detect file type based on extension
        val mimeType = when {
            filePath.endsWith(".jpg", true) || filePath.endsWith(".jpeg", true) -> "image/jpeg"
            filePath.endsWith(".png", true) -> "image/png"
            else -> {
                println("Error: Invalid file type. Only JPEG, PNG, and PDF are allowed!")
                return null
            }
        }

        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull()) // Convert file to RequestBody
        return MultipartBody.Part.createFormData("foto_selfie", file.name, requestFile) // Set part name
    }
}