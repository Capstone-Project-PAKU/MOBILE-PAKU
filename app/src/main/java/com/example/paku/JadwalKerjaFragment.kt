package com.example.paku

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import java.util.Calendar
import java.util.Locale

class JadwalKerjaFragment : Fragment() {

    private lateinit var tvSelectMonth: TextView
    private lateinit var calendarSection: LinearLayout
    private lateinit var btnArrowDown: ImageButton // Tombol panah untuk memilih bulan dan tahun
    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleItemAdapter: ScheduleItemAdapter
    private lateinit var prefs: SharedPreferences
    private lateinit var scheduleBtn: Button
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var userOccupationTv: TextView
    private lateinit var notFoundTv: TextView
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_jadwal_kerja, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi komponen
        tvSelectMonth = view.findViewById(R.id.tvSelectMonth)
        calendarSection = view.findViewById(R.id.calendarSection)
        btnArrowDown = view.findViewById(R.id.btnArrowDown)
        recyclerView = view.findViewById(R.id.rvRekapJadwalKerja)
        scheduleBtn = view.findViewById(R.id.getScheduleBtn)
        userOccupationTv = view.findViewById(R.id.userOccupationTv)
        notFoundTv = view.findViewById(R.id.tvNotFound)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchUserProfile(accessToken)
        fetchUserSchedule(accessToken, userId, null, null, null)

        // Set action ketika LinearLayout diklik
        calendarSection.setOnClickListener {
            showDatePickerDialog()
        }

        // Set action ketika tombol arrow diklik
        btnArrowDown.setOnClickListener {
            showDatePickerDialog()
        }

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tvSelectMonth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val tanggal = tvSelectMonth.text.toString()
                fetchUserSchedule(accessToken, userId, null, null, tanggal)
            }
        })

        scheduleBtn.setOnClickListener {
            resetRecyclerView()
            fetchUserSchedule(accessToken, userId, null, null, null)
            tvSelectMonth.text = "Select a date"
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Menampilkan DatePickerDialog dengan memilih tanggal, bulan, dan tahun
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Format hasil pilihan tanggal, bulan, dan tahun
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                tvSelectMonth.text = dateFormat.format(selectedCalendar.time)
            },
            currentYear, currentMonth, currentDay
        )

        // Menampilkan dialog untuk memilih tanggal
        datePickerDialog.show()
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

    private fun resetRecyclerView() {
        if (::scheduleItemAdapter.isInitialized) {
            scheduleItemAdapter.updateData(emptyList())
        }
        recyclerView.visibility = View.GONE
        notFoundTv.visibility = View.VISIBLE
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

    private fun capitalizeWords(input: String): String {
        return input.split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercaseChar() } }
    }
}
