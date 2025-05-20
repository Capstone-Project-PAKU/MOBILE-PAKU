package com.example.paku

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.paku.ui.popup.showPresenceFailedPopup
import com.example.paku.ui.popup.showPresenceSuccessPopup
import com.example.paku.ui.viewmodel.PresenceViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PresensiDalamKlinikFragment : Fragment() {

    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var clockInBtn: Button
    private lateinit var clockOutBtn: Button
    private lateinit var clockInTimeTv: TextView
    private lateinit var clockOutTimeTv: TextView
    private lateinit var prefs: SharedPreferences
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var presenceIn: String
    private lateinit var clinicBSSID: String
    private lateinit var validationStatus: TextView
    private lateinit var validationIcon: ImageView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingOverlay: FrameLayout

    private val presenceViewModel: PresenceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_presensi_dalam_klinik, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clockInBtn = view.findViewById(R.id.clockInBtn)
        clockOutBtn = view.findViewById(R.id.clockOutBtn)
        clockInTimeTv = view.findViewById(R.id.clockInTimeTV)
        clockOutTimeTv = view.findViewById(R.id.clockOutTimeTV)
        validationStatus = view.findViewById(R.id.presenceValidationStatus)
        validationIcon = view.findViewById(R.id.presenceValidationIcon)
        loadingOverlay = view.findViewById(R.id.loadingOverlay)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)

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

        if (!isConnectedToWiFi()) {
            showPresenceFailedPopup(view, null)
        }

        clockInBtn.setOnClickListener {
            val bssid = getWifiBSSID()
            val date = getCurrentDate()
            val time = "07:10:00"
            presenceIn(userId, bssid, date, time, view)
        }

        clockOutBtn.setOnClickListener {
            val bssid = getWifiBSSID()
            val date = getCurrentDate()
            val time = "14:10:00"
            presenceOut(userId, bssid, date, time, view)
        }

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            parentFragmentManager.popBackStack() // Kembali ke fragment sebelumnya
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

    override fun onResume() {
        super.onResume()
        getCurrentPresence(userId)
    }

    private fun presenceIn(
        id_user: String,
        bssid: String,
        date: String,
        time: String,
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
        presenceViewModel.clockIn_Inside(id_user, date, time) { success, message, data ->
            if (success) {
                prefs.edit().putString("presenceIn", data?.waktu_masuk).apply()
                showPresenceSuccessPopup(view, message, this)
                getCurrentPresence(id_user)
            } else {
                showPresenceFailedPopup(view, message)
            }
        }
    }

    private fun presenceOut(
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
        presenceViewModel.clockOut_Inside(id_user, date, time) { success, message, _ ->
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

    @SuppressLint("SetTextI18n")
    private fun getCurrentPresence(userId: String) {
        presenceViewModel.getCurrentPresence(userId) { success, _, data ->
            if (success) {
                if (data != null && data.jenis_presensi == "dalam") {
                    prefs.edit().remove("presenceIn").apply()
                    clockOutTimeTv.text = data.waktu_keluar
                    clockInTimeTv.text = data.waktu_masuk
                    validationStatus.text = "Status Validasi: " + data.validasi_masuk
                    validationIcon.setImageResource(R.drawable.icon_accept)
                } else {
                    presenceIn = prefs.getString("presenceIn", "-").toString()
                    clockInTimeTv.text = presenceIn
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
}


