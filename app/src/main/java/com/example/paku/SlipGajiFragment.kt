package com.example.paku

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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paku.data.api.RetrofitClient
import com.example.paku.data.model.list.PayrollData
import com.example.paku.ui.adapter.PayrollItemAdapter
import com.example.paku.ui.popup.showPhotoViewer
import com.example.paku.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SlipGajiFragment : Fragment() {

    private lateinit var tvSelectMonth: TextView
    private lateinit var calendarSection: LinearLayout
    private lateinit var btnArrowDown: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var payrollItemAdapter: PayrollItemAdapter
    private lateinit var prefs: SharedPreferences
    private lateinit var accessToken: String
    private lateinit var userId: String
    private lateinit var userOccupationTv: TextView
    private lateinit var notFoundTv: TextView
    private lateinit var getPayrollBtn: Button
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_slip_gaji, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View Calendar
        tvSelectMonth = view.findViewById(R.id.tvSelectMonth)
        calendarSection = view.findViewById(R.id.calendarSection)
        btnArrowDown = view.findViewById(R.id.btnArrowDown)
        recyclerView = view.findViewById(R.id.rvSlipGaji)
        notFoundTv = view.findViewById(R.id.tvNotFound)
        userOccupationTv = view.findViewById(R.id.userOccupationTv)
        getPayrollBtn = view.findViewById(R.id.getPayrollBtn)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        accessToken = prefs.getString("accessToken", null).toString()
        userId = prefs.getString("userId", null).toString()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchUserProfile()
        fetchUserPayroll(userId, null, null)

        // Set action ketika diklik untuk memilih bulan dan tahun
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

        tvSelectMonth.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val res = getMonthAndYear(tvSelectMonth.text.toString())
                val month = res?.first
                val year = res?.second
                fetchUserPayroll(userId, month, year)
            }
        })

        getPayrollBtn.setOnClickListener {
            resetRecyclerView()
            fetchUserPayroll(userId, null, null)
            tvSelectMonth.text = "Select a month"
        }
    }

    private fun showMonthYearPicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        // Menampilkan dialog untuk memilih bulan dan tahun
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

        // Membuat dialog untuk pemilihan bulan & tahun
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Bulan dan Tahun")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val selectedMonth = monthPicker.value
                val selectedYear = yearPicker.value
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, 1)

                // Menampilkan bulan dan tahun yang dipilih
                val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                tvSelectMonth.text = dateFormat.format(selectedCalendar.time)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun fetchUserPayroll(
        userId: String,
        monthFilter: String?,
        yearFilter: String?
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance().getUserPayroll(userId, monthFilter, yearFilter)
                if (response.isSuccessful) {
                    val payrollList = response.body()?.data ?: emptyList()

                    if (payrollList.isNotEmpty()) {
                        showRecyclerView(payrollList)
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

    private fun showRecyclerView(payrollList: List<PayrollData>) {
        notFoundTv.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        if (::payrollItemAdapter.isInitialized) {
            payrollItemAdapter.updateData(payrollList)
        } else {
            payrollItemAdapter = PayrollItemAdapter(
                payrollList,
                onShowInvoicePhoto = { anchorView, urlPath ->
                    showPhotoViewer(anchorView, "Bukti Pembayaran", urlPath)
                }
            )
            recyclerView.adapter = payrollItemAdapter
        }
    }

    private fun resetRecyclerView() {
        if (::payrollItemAdapter.isInitialized) {
            payrollItemAdapter.updateData(emptyList())  // 🔹 Clear adapter to avoid stale data
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
