package com.example.paku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class SlipGajiFragment : Fragment() {

    private lateinit var tvSelectMonth: TextView
    private lateinit var calendarSection: LinearLayout

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

        // Set action ketika diklik untuk memilih bulan dan tahun
        calendarSection.setOnClickListener {
            showMonthYearPicker()
        }

        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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
}
