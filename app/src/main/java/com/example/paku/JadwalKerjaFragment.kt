package com.example.paku

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.list.ScheduleDetailData
import com.example.paku.ui.adapter.ScheduleItemAdapter
import com.example.paku.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class JadwalKerjaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleItemAdapter: ScheduleItemAdapter
    private lateinit var prefs: SharedPreferences
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var userOccupationTv: TextView
    private lateinit var notFoundTv: TextView
    private lateinit var calendar: CalendarView
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_jadwal_kerja, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi komponen
        recyclerView = view.findViewById(R.id.rvRekapJadwalKerja)
        userOccupationTv = view.findViewById(R.id.userOccupationTv)
        notFoundTv = view.findViewById(R.id.tvNotFound)
        calendar = view.findViewById(R.id.calendarView)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchUserProfile(accessToken)
        fetchUserSchedule(accessToken, userId, null, null, getCurrentDate())

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val result = dateFormat.format(selectedCalendar.time)
            fetchUserSchedule(accessToken, userId, null, null, result)
        }
    }

    private fun fetchUserSchedule(
        token: String,
        userId: String,
        monthFilter: String?,
        yearFilter: String?,
        dateFilter: String?
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getUserSchedule("Bearer $token", userId, monthFilter, yearFilter, dateFilter)
                if (response.isSuccessful) {
                    val scheduleList = response.body()?.data ?: emptyList()
                    
                    if (scheduleList.isNotEmpty()) {
                        showRecyclerView(scheduleList)
                    } else {
                        showEmptyState()
                    }
                } else {
                    showEmptyState()
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Unexpected error: ${e.message}")
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecyclerView(scheduleList: List<ScheduleDetailData>) {
        notFoundTv.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        if (::scheduleItemAdapter.isInitialized) {
            scheduleItemAdapter.updateData(scheduleList)
        } else {
            scheduleItemAdapter = ScheduleItemAdapter(scheduleList)
            recyclerView.adapter = scheduleItemAdapter
        }
    }

    private fun showEmptyState() {
        recyclerView.visibility = View.GONE
        notFoundTv.visibility = View.VISIBLE
    }

    private fun fetchUserProfile(token: String) {
        userViewModel.getProfile(token) { success, userData ->
            if (success) {
                val userOccupation = userData?.jabatan?.let { capitalizeWords(it) }
                userOccupationTv.text = userOccupation
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

    private fun capitalizeWords(input: String): String {
        return input.split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercaseChar() } }
    }
}
