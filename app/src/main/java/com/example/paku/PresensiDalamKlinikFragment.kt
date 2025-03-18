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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.data.api.RetrofitClient
import com.example.paku.ui.adapter.PresenceItemAdapter
import com.example.paku.ui.popup.showPresenceFailedPopup
import com.example.paku.ui.popup.showPresenceSuccessPopup
import com.example.paku.ui.viewmodel.PresenceViewModel
import com.example.paku.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
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
    private lateinit var validationStatus: TextView
    private lateinit var validationIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenceItemAdapter: PresenceItemAdapter
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
        recyclerView = view.findViewById(R.id.rvPresence)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

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
            presenceIn(accessToken, userId, bssid, date, time, view)
        }

        clockOutBtn.setOnClickListener {
            val bssid = getWifiBSSID()
            val date = getCurrentDate()
            val time = "14:10:00"
            presenceOut(accessToken, userId, bssid, date, time, view)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        fetchPresence(accessToken, userId)

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            parentFragmentManager.popBackStack() // Kembali ke fragment sebelumnya
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentPresence(accessToken)
    }

    private fun presenceIn(
        token: String,
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
        presenceViewModel.clockIn_Inside(token, id_user, date, time) { success, message, data ->
            if (success) {
                prefs.edit().putString("presenceIn", data?.waktu_masuk).apply()
                showPresenceSuccessPopup(view, message)
                getCurrentPresence(token)
            } else {
                showPresenceFailedPopup(view, message)
            }
        }
    }

    private fun presenceOut(
        token: String,
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
        presenceViewModel.clockOut_Inside(token, id_user, date, time) { success, message, data ->
            if (success){
                showPresenceSuccessPopup(view, message)
                getCurrentPresence(token)
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
        val bssid = wifiInfo.bssid

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
    private fun getCurrentPresence(token: String) {
        presenceViewModel.getCurrentPresence(token) { success, message, data ->
            if (success) {
                if (data != null) {
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

    private fun fetchPresence(token: String, userId: String){
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getUserPresence(token, userId)
                if (response.isSuccessful) {
                    response.body()?.let { presenceResponse ->
                        val presenceList = presenceResponse.data
                        presenceItemAdapter = PresenceItemAdapter(presenceList)
                        recyclerView.adapter = presenceItemAdapter
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Unexpected error: ${e.message}")
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,  // animasi masuk
                R.anim.slide_out_left,  // animasi keluar
                R.anim.slide_in_left,   // animasi kembali masuk
                R.anim.slide_out_right  // animasi kembali keluar
            )
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

}


