package com.example.paku

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.paku.ui.popup.showFailedPopup
import com.example.paku.ui.popup.showPresenceFailedPopup
import com.example.paku.ui.popup.showPresenceSuccessPopup
import com.example.paku.ui.viewmodel.PresenceViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PresensiDalamKlinikFragment : Fragment() {

    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var cameraBtn: Button
    private lateinit var clockInBtn: Button
    private lateinit var clockOutBtn: Button
    private lateinit var photoPreview: ImageView
    private lateinit var prefs: SharedPreferences
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var clinicBSSID: String
    private lateinit var validationStatus: TextView
    private lateinit var validationIcon: ImageView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingOverlay: FrameLayout

    private var imageUri: String? = null
    private var hasClockInToday: Boolean = false
    private val presenceViewModel: PresenceViewModel by viewModels()

    private val cameraResultLaucher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.getStringExtra("imagePath") ?: return@registerForActivityResult
            photoPreview.setImageURI(Uri.parse(imageUri))
        } else {
            Toast.makeText(requireContext(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_presensi_dalam_klinik, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraBtn = view.findViewById(R.id.btnCamera)
        clockInBtn = view.findViewById(R.id.btnClockIn)
        clockOutBtn = view.findViewById(R.id.btnClockOut)
        validationStatus = view.findViewById(R.id.presenceValidationStatus)
        validationIcon = view.findViewById(R.id.presenceValidationIcon)
        loadingIndicator = requireActivity().findViewById(R.id.loadingIndicator)
        loadingOverlay = requireActivity().findViewById(R.id.loadingOverlay)
        photoPreview = view.findViewById(R.id.selfiePreview)

        (activity as? MainActivity)?.showGlobalLoading(true) // saat loading mulai
        (activity as? MainActivity)?.showGlobalLoading(false) // saat loading selesai

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        getDetailInfo()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }

        cameraBtn.setOnClickListener {
            try {
                val intent = Intent(requireContext(), CameraActivity::class.java)
                cameraResultLaucher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal membuka kamera", Toast.LENGTH_SHORT).show()
            }
        }

        setUpButtonListeners()

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpButtonListeners() {
        clockInBtn.setOnClickListener(null)

        clockOutBtn.setOnClickListener(null)

        clockInBtn.setOnClickListener {
            validateAndSubmit(true)
        }

        clockOutBtn.setOnClickListener {
            validateAndSubmit(false)
        }

        clockInBtn.isClickable = true
        clockOutBtn.isClickable = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateAndSubmit(isClockIn: Boolean) {
        if (imageUri == null) {
            showFailedPopup(requireView(), "Foto tidak boleh kosong")
            return
        }

        showLoading()

        val date = getCurrentDate()
        val photo = getFilePart(imageUri!!)
        val bssid = getWifiBSSID()
        Log.d("bssid", bssid)
        if (isClockIn) {
            val time = "07:10:00"
            clockIn(photo!!, userId, bssid, date, time, requireView())
        } else {
            val time = "14:10:00"
            clockOut(photo!!, userId, bssid, date, time, requireView())
        }
    }

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        loadingOverlay.visibility = View.VISIBLE
        clockInBtn.isEnabled = false
        clockOutBtn.isEnabled = false
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        loadingOverlay.visibility = View.GONE
        clockInBtn.isEnabled = true
        clockOutBtn.isEnabled = true
    }

    private fun updateButtonsState() {
        if (hasClockInToday) {
            clockInBtn.visibility = View.GONE
            clockOutBtn.visibility = View.VISIBLE
            clockOutBtn.isEnabled = true
            clockOutBtn.isClickable = true
        } else {
            clockInBtn.visibility = View.VISIBLE
            clockOutBtn.visibility = View.GONE
            clockInBtn.isEnabled = true
            clockInBtn.isClickable = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        getCurrentPresence(userId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clockIn(
        photo: MultipartBody.Part,
        id_user: String,
        bssid: String,
        date: String,
        time: String,
        view: View
    ) {
        if (!isConnectedToWiFi()) {
            showPresenceFailedPopup(view, null)
            hideLoading()
            return
        }

        if (bssid != clinicBSSID) {
            showPresenceFailedPopup(view, "BSSID tidak sesuai, pastikan anda terhubung dengan Wifi yang benar!")
            hideLoading()
            return
        }
        presenceViewModel.clockIn_Inside(photo, id_user, date, time) { success, message, _ ->
            hideLoading()
            if (success) {
                showPresenceSuccessPopup(view, message, this)
                getCurrentPresence(id_user)
            } else {
                showPresenceFailedPopup(view, message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clockOut(
        photo: MultipartBody.Part,
        id_user: String,
        bssid: String,
        date: String,
        time:String,
        view: View
    ) {
        if (!isConnectedToWiFi()) {
            showPresenceFailedPopup(view, null)
            return
        }

        if (bssid != clinicBSSID) {
            showPresenceFailedPopup(view, "BSSID tidak sesuai, pastikan anda terhubung dengan Wifi yang benar!")
            return
        }

        presenceViewModel.clockOut_Inside(photo, id_user, date, time) { success, message, _ ->
            hideLoading()
            if (success){
                showPresenceSuccessPopup(view, message, this)
                getCurrentPresence(id_user)
            } else {
                showPresenceFailedPopup(view, message)
            }
        }
    }

    private fun isConnectedToWiFi(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun getWifiBSSID(): String {
        val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val bssid = wifiInfo.bssid ?: "-"
        return bssid
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        return  formattedDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formattedTime = currentTime.format(formatter)

        return formattedTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun getCurrentPresence(userId: String) {
        presenceViewModel.getCurrentPresence(userId) { success, _, data ->
            if (success) {
                hasClockInToday = data?.waktu_masuk != null

                setUpButtonListeners()
                updateButtonsState()

                if (data?.validasi_masuk == "setuju" && data.validasi_keluar == "setuju") {
                    validationStatus.text = "Status Validasi: setuju"
                    validationIcon.setImageResource(R.drawable.icon_accept)
                } else if (data?.validasi_masuk == "tolak" || data?.validasi_keluar == "tolak") {
                    validationStatus.text = "Status Validasi: tolak"
                    validationIcon.setImageResource(R.drawable.icon_decline)
                } else if (data?.validasi_masuk == "pending" || data?.validasi_keluar == "pending") {
                    validationStatus.text = "Status Validasi: " + "pending"
                    validationIcon.setImageResource(R.drawable.icon_pending)
                }
            }
        }
    }

    private fun getDetailInfo() {
        presenceViewModel.getDetailInfo() { success, data, error ->
            if (success) {
                data?.let {
                    clinicBSSID = data.bssid
                }
            } else {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
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


