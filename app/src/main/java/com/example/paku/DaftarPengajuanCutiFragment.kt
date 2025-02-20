package com.example.paku

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class DaftarPengajuanCutiFragment : Fragment() {

    private lateinit var tvSelectMonth: TextView
    private lateinit var calendarSection: LinearLayout
    private lateinit var btnArrowDown: ImageButton // Tombol panah untuk memilih bulan dan tahun

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_daftar_pengajuan_cuti, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi komponen
        tvSelectMonth = view.findViewById(R.id.tvSelectMonth)
        calendarSection = view.findViewById(R.id.calendarSection)
        btnArrowDown = view.findViewById(R.id.btnArrowDown) // Ambil tombol arrow down dari layout
        val imgBack = view.findViewById<ImageView>(R.id.back)

        // Set action ketika LinearLayout diklik
        calendarSection.setOnClickListener {
            showDatePickerDialog()
        }

        // Set action ketika tombol arrow diklik
        btnArrowDown.setOnClickListener {
            showDatePickerDialog()
        }

        // Tombol kembali ke fragment sebelumnya
        imgBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Menampilkan DatePickerDialog dengan memilih tanggal, bulan, dan tahun
        val datePickerDialog = DatePickerDialog(
            requireContext(), // Menggunakan requireContext() untuk Fragment
            { _, year, month, dayOfMonth ->
                // Format hasil pilihan tanggal, bulan, dan tahun
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                tvSelectMonth.text = dateFormat.format(selectedCalendar.time)
            },
            currentYear, currentMonth, currentDay
        )

        // Menampilkan dialog untuk memilih tanggal
        datePickerDialog.show()
    }
}
