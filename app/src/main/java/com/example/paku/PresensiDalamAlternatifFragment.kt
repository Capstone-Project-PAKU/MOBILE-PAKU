package com.example.paku

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.example.paku.ui.popup.showPresenceSuccessPopup
import com.example.paku.ui.viewmodel.PresenceViewModel
import com.example.paku.utils.DeviceUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PresensiDalamAlternatifFragment : Fragment() {
    private lateinit var cameraBtn: Button
    private lateinit var clockInBtn: Button
    private lateinit var clockOutBtn: Button
    private lateinit var photoPreview: ImageView
    private lateinit var prefs: SharedPreferences
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var validationStatus: TextView
    private lateinit var validationIcon: ImageView
    private lateinit var locationManager: LocationManager
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingOverlay: FrameLayout

    private var imageUri: String? = null
    private var locationJSON = JSONObject()
    private val presenceViewModel: PresenceViewModel by viewModels()
    private var latitude = 0.0
    private var longitude = 0.0
    private var hasClockInToday: Boolean = false
    private var isLocationReady: Boolean = false


    private val locationListener = LocationListener { location ->
        val isMockLocationUsed = DeviceUtils.isFakeGpsCurrentlyUsed(location)
        if (isMockLocationUsed) {
            disableLocationBasedFeatures()
            showFailedPopup(requireView(),
                "Terdeteksi aplikasi fake GPS aktif di perangkat anda")
            isLocationReady = false
            return@LocationListener
        }  else {
            latitude = location.latitude
            longitude = location.longitude
            locationJSON.put("latitude", latitude)
            locationJSON.put("longitude", longitude)
            isLocationReady = true
        }
    }

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
        return inflater.inflate(R.layout.fragment_presensi_dalam_alternatif, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraBtn = view.findViewById(R.id.btnCamera)
        clockInBtn = view.findViewById(R.id.btnClockInAlt)
        clockOutBtn = view.findViewById(R.id.btnClockOutAlt)
        photoPreview = view.findViewById(R.id.selfiePreview)
        validationIcon = view.findViewById(R.id.presenceAltValidationIcon)
        validationStatus = view.findViewById(R.id.presenceAltValidationStatus)
        loadingIndicator = requireActivity().findViewById(R.id.loadingIndicator)
        loadingOverlay = requireActivity().findViewById(R.id.loadingOverlay)

        (activity as? MainActivity)?.showGlobalLoading(true) // saat loading mulai
        (activity as? MainActivity)?.showGlobalLoading(false) // saat loading selesai

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        resetClockInIdentifier()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        getCurrentLocation()
        getCurrentPresence(userId)

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
        imgBack.setOnClickListener { parentFragmentManager.popBackStack() }

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

    private fun disableLocationBasedFeatures() {
        clockInBtn.isEnabled = false
        clockInBtn.isClickable = false
        clockOutBtn.isClickable = false
        clockOutBtn.isEnabled = false
        cameraBtn.isEnabled = false
        cameraBtn.isClickable = false
    }

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        loadingOverlay.visibility = View.VISIBLE
        clockInBtn.isEnabled = false
        clockOutBtn.isEnabled = false
        cameraBtn.isEnabled = false
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        loadingOverlay.visibility = View.GONE
        updateButtonsState()
        cameraBtn.isEnabled = true
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
    private fun validateAndSubmit(isClockIn: Boolean) {
        if (imageUri == null) {
            showFailedPopup(requireView(), "Foto tidak boleh kosong")
            return
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showFailedPopup(requireView(), "Pastikan fitur GPS pada perangkat anda dihidupkan")
            return
        }

        if (!isLocationReady || (latitude == 0.0 && longitude == 0.0 && !locationJSON.has("latitude"))) {
            showFailedPopup(requireView(), "Mencari lokasi akurat. Mohon tunggu sebentar dan coba lagi.")
            getCurrentLocation()
            return
        }

        showLoading()

        val date = getCurrentDate()
        val photo = getFilePart(imageUri!!)
        val lokasi = locationJSON.toString()
        if (isClockIn) {
            val time = "07:10:00"
            clockInAlt(photo!!, userId, date, time, lokasi, requireView())
        } else {
            val time = "14:10:00"
            clockOutAlt(photo!!, userId, date, time, lokasi, requireView())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clockInAlt(
        photo: MultipartBody.Part,
        id_user: String,
        date: String,
        time: String,
        lokasi: String,
        view: View
    ) {
        presenceViewModel.clockIn_Alternate(photo, id_user, date, time, lokasi) { success, message, _ ->
            hideLoading()
            if (success) {
                showPresenceSuccessPopup(view, message, this)
                getCurrentPresence(id_user)
            } else {
                showFailedPopup(view, message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clockOutAlt(
        photo: MultipartBody.Part,
        id_user: String,
        date: String,
        time: String,
        lokasi: String,
        view: View
    ) {
        presenceViewModel.clockOut_Alternate(photo, id_user, date, time, lokasi) { success, message, _ ->
            hideLoading()
            if (success) {
                showPresenceSuccessPopup(view, message, this)
                getCurrentPresence(id_user)
            } else {
                showFailedPopup(view, message)
            }
        }
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
                    validationStatus.text = "Status Validasi: " + data.validasi_masuk
                    validationIcon.setImageResource(R.drawable.icon_accept)
                } else if (data?.validasi_masuk == "tolak" || data?.validasi_keluar == "tolak") {
                    validationStatus.text = "Status Validasi: tolak"
                    validationIcon.setImageResource(R.drawable.icon_decline)
                } else if (data?.validasi_masuk == "pending" || data?.validasi_keluar == "pending") {
                    validationStatus.text = "Status Validasi: pending"
                    validationIcon.setImageResource(R.drawable.icon_pending)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Akses lokasi dibutuhkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLocationServiceOffDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Aktifkan Layanan Lokasi")
            .setMessage("Layanan lokasi perangkat Anda tidak aktif. Mohon aktifkan untuk menggunakan fitur presensi.")
            .setPositiveButton("Buka Pengaturan") { dialog, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(requireContext(), "Presensi memerlukan lokasi. Fitur dibatasi.", Toast.LENGTH_LONG).show()
                disableLocationBasedFeatures()
            }
            .show()
    }

    private fun getCurrentLocation() {
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isLocationServiceEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isLocationServiceEnabled) {
            showLocationServiceOffDialog()
            return
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L, // Update every 5 seconds
                10f,   // Update every 10 meters
                locationListener
            )
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        return  formattedDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun resetClockInIdentifier() {
        val lastReset =prefs.getString("last_reset_day", null)
        val today = LocalDate.now().toString()
        if (lastReset != today) {
            prefs.edit()
                .remove("waktu_masuk_alt")
                .putString("last_reset_date", today)
                .apply()
        }
    }

    private fun getFilePart(filePath: String): MultipartBody.Part? {
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