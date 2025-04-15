package com.example.paku

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.data.api.RetrofitClient
import com.example.paku.ui.adapter.PresenceItemAdapter
import com.example.paku.ui.popup.MapPopupFragment
import com.example.paku.ui.popup.showPhotoViewer
import com.example.paku.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class DetailRekapKehadiranFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var accessToken: String
    private lateinit var userId: String
    private var month: String? = null
    private var year: String? = null
    private lateinit var userOccupationTv: TextView
    private lateinit var title: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenceItemAdapter: PresenceItemAdapter
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail_rekap_kehadiran, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById(R.id.titleBulan)
        recyclerView = view.findViewById(R.id.rvDetailAbsensi)
        userOccupationTv = view.findViewById(R.id.userOccupationTv)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        month = arguments?.getString("month")
        year = arguments?.getString("year")

        title.text = "${month?.let { convertToMonth(it) }} ${year}"
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchUserProfile(accessToken)
        fetchPresence(accessToken, userId, month, year, 31)

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun convertToMonth(num: String): String? {
        val monthMap = mapOf(
            "1" to "Januari", "2" to "Februari", "3" to "Maret",
            "4" to "April", "5" to "Mei", "6" to "Juni",
            "7" to "Juli", "8" to "Agustus", "9" to "September",
            "10" to "Oktober", "11" to "November", "12" to "Desember"
        )

        return monthMap[num]
    }

    private fun fetchPresence(token: String, userId: String, month: String?, year: String?, limit: Int?){
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getUserPresence("Bearer $token", userId, month, year, limit)
                if (response.isSuccessful) {
                    response.body()?.let { presenceResponse ->
                        val presenceList = presenceResponse.data
                        presenceItemAdapter = PresenceItemAdapter(
                            presenceList,
                            onShowClockInPhoto = { anchorView, urlPath -> showPhotoViewer(anchorView, urlPath) },
                            onShowClockOutPhoto = { anchorView, urlPath -> showPhotoViewer(anchorView, urlPath) },
                            onShowClockInLocation = { location ->
                                val dialog = MapPopupFragment.newInstance(location)
                                dialog.show(requireActivity().supportFragmentManager, "Map Popup")
                            },
                            onShowClockOutLocation = { location ->
                                val dialog = MapPopupFragment.newInstance(location)
                                dialog.show(requireActivity().supportFragmentManager, "Map Popup")
                            }
                        )
                        recyclerView.adapter = presenceItemAdapter
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Unexpected error: ${e.message}")
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserProfile(token: String) {
        userViewModel.getProfile(token) { success, userData ->
            if (success) {
                val userOccupation = userData?.jabatan?.let { capitalizeWords(it) }
                userOccupationTv.text = userOccupation
            }
        }
    }

    private fun capitalizeWords(input: String): String {
        return input.split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercaseChar() } }
    }
}
