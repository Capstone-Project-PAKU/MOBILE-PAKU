package com.example.paku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menangani tombol back agar kembali ke halaman sebelumnya di bottom navigation
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNav.selectedItemId = R.id.home // Ganti sesuai menu sebelumnya
            }
        })

        val tvChange = view.findViewById<TextView>(R.id.changePW)
        tvChange.setOnClickListener {
            val changePasswordFragment = ChangePasswordFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, changePasswordFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
