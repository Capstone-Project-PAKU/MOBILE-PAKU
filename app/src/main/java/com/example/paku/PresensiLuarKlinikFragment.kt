package com.example.paku

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.paku.ui.popup.showFailedPopup
import com.example.paku.ui.popup.showPresenceSuccessPopup
import com.example.paku.ui.viewmodel.PresenceViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PresensiLuarKlinikFragment : Fragment() {
    private lateinit var cameraBtn: Button
    private lateinit var clockInBtn: Button
    private lateinit var clockOutBtn: Button
    private lateinit var tvPdfName: TextView
    private lateinit var btnUploadPdf: LinearLayout
    private lateinit var photoPreview: ImageView
    private lateinit var prefs: SharedPreferences
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var locationManager: LocationManager
    private lateinit var validationStatus: TextView
    private lateinit var validationIcon: ImageView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingOverlay: FrameLayout
    private var imageUri: String? = null
    private var pdfFile: String? = null
    private var pdfName: String? = null
    private var locationJSON = JSONObject()
    private val presenceViewModel: PresenceViewModel by viewModels()
    private var latitude = 0.0
    private var longitude = 0.0
    private var hasClockInToday: Boolean = false

    private val locationListener = LocationListener { location ->
        if (location.isFromMockProvider) {
            showFailedPopup(requireView(), "Anda menggunakan FakeGPS")
            clockInBtn.isEnabled = false
            clockInBtn.isClickable = false
            clockOutBtn.isEnabled = false
            clockOutBtn.isClickable = false
            cameraBtn.isEnabled = false
            cameraBtn.isClickable = false
        } else {
            latitude = location.latitude
            longitude = location.longitude
            locationJSON.put("latitude", latitude)
            locationJSON.put("longitude", longitude)
        }
    }

    private val cameraResultLaucher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.getStringExtra("imagePath")
            photoPreview.setImageURI(Uri.parse(imageUri))
        }
    }

    // ActivityResultLauncher untuk memilih file PDF
    private lateinit var selectPdfLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_presensi_luar_klinik, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View
        tvPdfName = view.findViewById(R.id.tvPdfName)
        btnUploadPdf = view.findViewById(R.id.uploadPdf)
        photoPreview = view.findViewById(R.id.selfiePreview)
        cameraBtn = view.findViewById(R.id.btnCamera)
        clockInBtn = view.findViewById(R.id.btnClockInOutside)
        clockOutBtn = view.findViewById(R.id.btnClockOutOutside)
        validationIcon = view.findViewById(R.id.presenceOutValidationIcon)
        validationStatus = view.findViewById(R.id.presenceOutValidationStatus)
        loadingIndicator = requireActivity().findViewById(R.id.loadingIndicator)
        loadingOverlay = requireActivity().findViewById(R.id.loadingOverlay)

        (activity as? MainActivity)?.showGlobalLoading(true) // saat loading mulai
        (activity as? MainActivity)?.showGlobalLoading(false) // saat loading selesai


        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        getCurrentLocation()
        getCurrentPresence(userId)

        cameraBtn.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            cameraResultLaucher.launch(intent)
        }

        setUpButtonListeners()

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Inisialisasi ActivityResultLauncher untuk memilih file PDF
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

        // Tombol untuk memilih file PDF
        btnUploadPdf.setOnClickListener {
            openFilePicker()
        }
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

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        loadingOverlay.visibility = View.VISIBLE
        clockInBtn.isEnabled = false
        clockOutBtn.isEnabled = false
        cameraBtn.isEnabled = false
        btnUploadPdf.isEnabled = false
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        loadingOverlay.visibility = View.GONE
        updateButtonsState()
        cameraBtn.isEnabled = true
        btnUploadPdf.isEnabled = true
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateAndSubmit(isClockIn: Boolean) {
        if (imageUri == null || pdfFile == null) {
            showFailedPopup(requireView(), "Foto dan Surat Tugas tidak boleh kosong")
            return
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showFailedPopup(requireView(), "Pastikan fitur GPS pada perangkat anda dihidupkan")
            return
        }

        showLoading()

        val date = getCurrentDate()
        val photo = getPhotoPart(imageUri!!)
        val document = getPdfPart(pdfFile!!)
        val lokasi = locationJSON.toString()
        if (isClockIn) {
            val time = "07:10:00"
            clockInOutside(photo!!, document!!, userId, date, time, lokasi, requireView())
        } else {
            val time = "14:10:00"
            clockOutOutside(photo!!, document!!, userId, date, time, lokasi, requireView())
        }
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clockInOutside(
        photo: MultipartBody.Part,
        document: MultipartBody.Part,
        id_user: String,
        date: String,
        time: String,
        lokasi: String,
        view: View
    ) {
        presenceViewModel.clockIn_Outside(photo, document, id_user, date, time, lokasi) { success, message, _ ->
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
    private fun clockOutOutside(
        photo: MultipartBody.Part,
        document: MultipartBody.Part,
        id_user: String,
        date: String,
        time: String,
        lokasi: String,
        view: View
    ) {
        presenceViewModel.clockOut_Outside(photo, document, id_user, date, time, lokasi) { success, message, _ ->
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
                    validationStatus.text = "Status Validasi: setuju"
                    validationIcon.setImageResource(R.drawable.icon_accept)
                } else if (data?.validasi_masuk == "tolak" || data?.validasi_keluar == "tolak") {
                    validationStatus.text = "Status Validasi: tolak"
                    validationIcon.setImageResource(R.drawable.icon_decline)
                } else if (data?.validasi_masuk == "pending" || data?.validasi_keluar == "pending"){
                    validationStatus.text = "Status Validasi: pending"
                    validationIcon.setImageResource(R.drawable.icon_pending)
                }
            }
        }
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

    private fun getCurrentLocation() {
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

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

    private fun getPhotoPart(filePath: String): MultipartBody.Part? {
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
                println("Error: Invalid file type. Only JPEG, PNG, are allowed!")
                return null
            }
        }

        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull()) // Convert file to RequestBody
        return MultipartBody.Part.createFormData("foto_selfie", file.name, requestFile) // Set part name
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
                println("Error: Invalid file type. Only JPEG, PNG, are allowed!")
                return null
            }
        }

        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull()) // Convert file to RequestBody
        return MultipartBody.Part.createFormData("foto_dokumen", file.name, requestFile) // Set part name
    }

    // Fungsi untuk memilih file PDF
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        selectPdfLauncher.launch(Intent.createChooser(intent, "Pilih file PDF"))
    }

    // Fungsi untuk mendapatkan nama file dari URI
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
}
