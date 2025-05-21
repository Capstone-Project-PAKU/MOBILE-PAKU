package com.example.paku

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.paku.ui.viewmodel.UserViewModel

class HomeFragment : Fragment() {

    private lateinit var dropdownPresensi: LinearLayout
    private lateinit var btnDropdownPresensi: ImageButton
    private var isDropdownPresensiVisible = false

    private lateinit var dropdownCuti: LinearLayout
    private lateinit var btnToggleDropdown: ImageButton
    private var isDropdownCutiVisible = false

    private lateinit var userHeaderTv: TextView
    private lateinit var userOccupationTv: TextView
    private lateinit var userPhotoProfile: ImageView
    private lateinit var pref: SharedPreferences
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userHeaderTv = view.findViewById(R.id.userHeaderTv)
        userOccupationTv = view.findViewById(R.id.userOccupationTv)
        userPhotoProfile = view.findViewById(R.id.profilePicture)

        pref = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        val accessToken = pref.getString("accessToken", null)

        if (!accessToken.isNullOrEmpty()) {
            fetchUserProfile()
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silahkan login kembali.", Toast.LENGTH_SHORT).show()
        }

        // Inisialisasi View Presensi
        val menuPresensi: LinearLayout = view.findViewById(R.id.menu_presensi)
        dropdownPresensi = view.findViewById(R.id.dropdownPresensi)
        btnDropdownPresensi = view.findViewById(R.id.btnDropdownPresensi)
        val presensiDalam: LinearLayout = view.findViewById(R.id.presensiDalam)
        val presensiLuar: LinearLayout = view.findViewById(R.id.presensiLuar)
        val riwayatPresensi: LinearLayout = view.findViewById(R.id.riwayatPresensi)

        // Inisialisasi View Pengajuan Cuti
        val menuCuti: LinearLayout = view.findViewById(R.id.menu_cuti)
        dropdownCuti = view.findViewById(R.id.dropdownPengajuanCuti)
        btnToggleDropdown = view.findViewById(R.id.btnToggleDropdown)
        val daftarCuti: LinearLayout = view.findViewById(R.id.daftarPengajuanCuti)
        val cutiSakit: LinearLayout = view.findViewById(R.id.PengajuanCuti)

        // Inisialisasi View Cek Rekap Kehadiran, Jadwal Kerja, dan Slip Gaji
        val menuRekapKehadiran: LinearLayout = view.findViewById(R.id.menu_rekap_kehadiran)
        val menuJadwalKerja: LinearLayout = view.findViewById(R.id.menu_jadwal_kerja)
        val menuSlipGaji: LinearLayout = view.findViewById(R.id.menu_slip_gaji)

        // Toggle dropdown saat tombol dropdown presensi diklik
        btnDropdownPresensi.setOnClickListener {
            isDropdownPresensiVisible = !isDropdownPresensiVisible
            toggleDropdown(dropdownPresensi, isDropdownPresensiVisible)
            btnDropdownPresensi.setImageResource(
                if (isDropdownPresensiVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            )
        }

        menuPresensi.setOnClickListener {
            isDropdownPresensiVisible = !isDropdownPresensiVisible
            toggleDropdown(dropdownPresensi, isDropdownPresensiVisible)
            btnDropdownPresensi.setImageResource(
                if (isDropdownPresensiVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            )
        }

        btnDropdownPresensi.setOnClickListener {
            menuPresensi.performClick() // agar klik tombol sama efeknya dengan klik seluruh layout
        }


        // Aksi saat "Presensi Dalam Klinik" diklik
        presensiDalam.setOnClickListener {
            navigateToFragment(PresensiDalamKlinikFragment())
        }

        // Aksi saat "Presensi Luar Klinik" diklik
        presensiLuar.setOnClickListener {
            navigateToFragment(PresensiLuarKlinikFragment())
        }

        riwayatPresensi.setOnClickListener {
            navigateToFragment(RiwayatPresensiFragment())
        }

        // Toggle dropdown saat tombol dropdown cuti diklik
        btnToggleDropdown.setOnClickListener {
            isDropdownCutiVisible = !isDropdownCutiVisible
            toggleDropdown(dropdownCuti, isDropdownCutiVisible)
            btnToggleDropdown.setImageResource(
                if (isDropdownCutiVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            )
        }

        menuCuti.setOnClickListener {
            isDropdownCutiVisible = !isDropdownCutiVisible
            toggleDropdown(dropdownCuti, isDropdownCutiVisible)
            btnToggleDropdown.setImageResource(
                if (isDropdownCutiVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            )
        }

        btnToggleDropdown.setOnClickListener {
            menuCuti.performClick()
        }

        // Aksi saat "Daftar Pengajuan Cuti" diklik
        daftarCuti.setOnClickListener {
            navigateToFragment(DaftarPengajuanCutiFragment())
        }

        // Aksi saat "Pengajuan Cuti Sakit" diklik
        cutiSakit.setOnClickListener {
            navigateToFragment(PengajuanCutiFragment())
        }

        // Aksi saat "Cek Rekap Kehadiran" diklik
        menuRekapKehadiran.setOnClickListener {
            navigateToFragment(CekRekapKehadiranFragment())
        }

        // Aksi saat "Jadwal Kerja" diklik
        menuJadwalKerja.setOnClickListener {
            navigateToFragment(JadwalKerjaFragment())
        }

        // Aksi saat "Slip Gaji" diklik
        menuSlipGaji.setOnClickListener {
            navigateToFragment(SlipGajiFragment())
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,  // Animasi masuk
                R.anim.slide_out_left,  // Animasi keluar
                R.anim.slide_in_left,   // Animasi masuk saat kembali
                R.anim.slide_out_right  // Animasi keluar saat kembali
            )
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun toggleDropdown(view: View, isVisible: Boolean) {
        // Pastikan ukuran view sudah diukur sebelum animasi
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val targetHeight = view.measuredHeight

        val startHeight = if (isVisible) 0 else targetHeight
        val endHeight = if (isVisible) targetHeight else 0

        val animator = ValueAnimator.ofInt(startHeight, endHeight).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                val layoutParams = view.layoutParams
                layoutParams.height = value
                view.layoutParams = layoutParams
            }
        }
        animator.start()

        // Atur visibilitas saat animasi selesai
        if (isVisible) {
            view.visibility = View.VISIBLE
        } else {
            animator.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    view.visibility = View.GONE
                }
            })
        }
    }

    private fun fetchUserProfile() {
        userViewModel.getProfile() { success, userData ->
            if (success) {
                val userHeader = userData?.username
                val userOccupation = userData?.jabatan?.let { capitalizeWords(it) }
                userHeaderTv.text = userHeader
                userOccupationTv.text = userOccupation

                if (userData?.j_kelamin == "Wanita"){
                    userPhotoProfile.setImageResource(R.drawable.icon_profile_picture_woman)
                }
            }
        }
    }

    private fun capitalizeWords(input: String): String {
        return input.split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercaseChar() } }
    }
}
