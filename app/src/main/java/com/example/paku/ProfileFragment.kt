package com.example.paku

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.paku.ui.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var fullnameEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var occupationEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var pref: SharedPreferences
    private lateinit var logoutTv: TextView
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameEditText = view.findViewById(R.id.usernameProfileField)
        fullnameEditText = view.findViewById(R.id.fullnameProfileField)
        genderEditText = view.findViewById(R.id.genderProfileField)
        occupationEditText = view.findViewById(R.id.occupationProfileField)
        phoneEditText = view.findViewById(R.id.phoneProfileField)
        logoutTv = view.findViewById(R.id.logoutTv)

        pref = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        val accessToken = pref.getString("accessToken", null)
        val refreshToken = pref.getString("refreshToken", null)

        val text = "Logout"
        val spannableString = SpannableString(text)

        val startIndex = spannableString.indexOf(text)
        val endIndex = startIndex + text.length

        val clickableSpan = object: ClickableSpan() {
            override fun onClick(widget: View) {
                logout(refreshToken!!)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }

        }

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        logoutTv.text = spannableString
        logoutTv.movementMethod = LinkMovementMethod.getInstance()

        if (!accessToken.isNullOrEmpty()) {
            fetchUserProfile(accessToken)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silahkan login kembali.", Toast.LENGTH_SHORT).show()
        }

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

    private fun logout(refreshToken: String) {
        userViewModel.logout(refreshToken) { success, message ->
            if (success) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                pref.edit()
                    .remove("accessToken")
                    .remove("refreshToken")
                    .apply()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserProfile(token: String) {
        userViewModel.getProfile(token) { success, userData ->
            if (success) {
                usernameEditText.setText(userData?.username)
                fullnameEditText.setText(userData?.nama_pegawai)
                genderEditText.setText(userData?.j_kelamin)
                occupationEditText.setText(userData?.jabatan)
                phoneEditText.setText(userData?.telp)
            }
        }
    }
}
