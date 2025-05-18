package com.example.paku

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class RiwayatPresensiFragment : Fragment() {
    private lateinit var prefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var userOccupationTv: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenceItemAdapter: PresenceItemAdapter
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment
        return inflater.inflate(R.layout.fragment_riwayat_presensi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        userId = prefs.getString("userId", null).toString()

        recyclerView = view.findViewById(R.id.rvRekapAbsensi)
        userOccupationTv = view.findViewById(R.id.userOccupationTv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchPresence(userId, null, null, 31)
        fetchUserProfile()

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun fetchPresence(userId: String, month: String?, year: String?, limit: Int?){
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance().getUserPresence(userId, month, year, limit)
                if (response.isSuccessful) {
                    response.body()?.let { presenceResponse ->
                        val presenceList = presenceResponse.data
                        presenceItemAdapter = PresenceItemAdapter(
                            presenceList,
                            onShowClockInPhoto = { anchorView, urlPath -> showPhotoViewer(anchorView, "Foto Selfie Masuk", urlPath) },
                            onShowClockOutPhoto = { anchorView, urlPath -> showPhotoViewer(anchorView, "Foto Selfie Keluar", urlPath) },
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

    private fun fetchUserProfile() {
        userViewModel.getProfile() { success, userData ->
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

