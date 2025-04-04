package com.example.paku

import android.app.AlertDialog
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
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.list.WorkLeaveData
import com.example.paku.data.model.list.WorkingRecapData
import com.example.paku.ui.adapter.LeaveItemAdapter
import com.example.paku.ui.adapter.PresenceRecapItemAdapter
import com.example.paku.ui.adapter.ScheduleItemAdapter
import com.example.paku.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CekRekapKehadiranFragment : Fragment() {

    private lateinit var tvSelectMonth: TextView
    private lateinit var calendarSection: LinearLayout
    private lateinit var btnArrowDown: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenceRecapItemAdapter: PresenceRecapItemAdapter
    private lateinit var prefs: SharedPreferences
    private lateinit var recapBtn: Button
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var userOccupationTv: TextView
    private lateinit var notFoundTv: TextView
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_cek_rekap_kehadiran, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View dari Fragment
        tvSelectMonth = view.findViewById(R.id.tvSelectMonth)
        calendarSection = view.findViewById(R.id.calendarSection)
        btnArrowDown = view.findViewById(R.id.btnArrowDown)
        userOccupationTv = view.findViewById(R.id.userOccupationTv)
        notFoundTv = view.findViewById(R.id.tvNotFound)
        recyclerView = view.findViewById(R.id.rvRekapAbsensi)
        recapBtn = view.findViewById(R.id.getRecapBtn)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchUserProfile(accessToken)
        fetchUserRecap(accessToken, userId, null, null)

        // Set action ketika LinearLayout diklik
        calendarSection.setOnClickListener {
            showMonthYearPicker()
        }

        // Set action ketika tombol arrow diklik
        btnArrowDown.setOnClickListener {
            showMonthYearPicker()
        }

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        tvSelectMonth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val res = getMonthAndYear(tvSelectMonth.text.toString())
                val month = res?.first
                val year = res?.second
                fetchUserRecap(accessToken, userId, month, year)
            }
        })

        recapBtn.setOnClickListener {
            resetRecyclerView()
            fetchUserRecap(accessToken, userId, null, null)
            tvSelectMonth.text = "Select a month"
        }
    }

    private fun showMonthYearPicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_month_year_picker, null)
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.pickerMonth)
        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.pickerYear)

        // Konfigurasi NumberPicker untuk Bulan
        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.displayedValues = arrayOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        monthPicker.value = currentMonth

        // Konfigurasi NumberPicker untuk Tahun
        val minYear = 2000
        val maxYear = 2100
        yearPicker.minValue = minYear
        yearPicker.maxValue = maxYear
        yearPicker.value = currentYear

        // Membuat dialog pemilih bulan & tahun
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Bulan dan Tahun")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                val selectedMonth = monthPicker.value
                val selectedYear = yearPicker.value
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, 1)
                val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                tvSelectMonth.text = dateFormat.format(selectedCalendar.time)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun fetchUserRecap(
        token: String,
        userId: String,
        monthFilter: String?,
        yearFilter: String?
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getUserWorkingRecap("Bearer $token", userId, monthFilter, yearFilter)
                if (response.isSuccessful) {
                    val recapList = response.body()?.data ?: emptyList()

                    if (recapList.isNotEmpty()) {
                        showRecyclerView(recapList)
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

    private fun showRecyclerView(recapList: List<WorkingRecapData>) {
        notFoundTv.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        if (::presenceRecapItemAdapter.isInitialized) {
            presenceRecapItemAdapter.updateData(recapList)
        } else {
            presenceRecapItemAdapter = PresenceRecapItemAdapter(recapList)
            recyclerView.adapter = presenceRecapItemAdapter
        }
    }

    private fun resetRecyclerView() {
        if (::presenceRecapItemAdapter.isInitialized) {
            presenceRecapItemAdapter.updateData(emptyList())  // 🔹 Clear adapter to avoid stale data
        }
        recyclerView.visibility = View.GONE  // 🔹 Hide RecyclerView momentarily
        notFoundTv.visibility = View.VISIBLE  // 🔹 Show loading state until data is available
    }

    private fun showEmptyState() {
        recyclerView.visibility = View.GONE
        notFoundTv.visibility = View.VISIBLE
    }

    private fun getMonthAndYear(tanggal:String): Pair<String, String>? {
        val monthMap = mapOf(
            "January" to "1", "February" to "2", "March" to "3",
            "April" to "4", "May" to "5", "June" to "6",
            "July" to "7", "August" to "8", "September" to "9",
            "October" to "10", "November" to "11", "December" to "12"
        )

        val date = tanggal.split(" ")

        if (date.size != 2) return null

        val month = monthMap[date[0]] ?: return null
        val year = date[1]

        return month to year
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
