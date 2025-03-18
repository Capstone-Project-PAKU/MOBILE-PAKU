package com.example.paku

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.example.paku.ui.viewmodel.UserViewModel

class ChangePasswordFragment : Fragment() {
    private lateinit var currentPassEditText: EditText
    private lateinit var newPassEditText: EditText
    private lateinit var confirmPassEditText: EditText
    private lateinit var prefs: SharedPreferences
    private lateinit var saveBtn: Button
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment_change_password.xml
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)

        currentPassEditText = view.findViewById(R.id.curentPass)
        newPassEditText = view.findViewById(R.id.newPass)
        confirmPassEditText = view.findViewById(R.id.confirmPass)
        saveBtn = view.findViewById(R.id.btnChangePassSave)

        prefs = requireContext().getSharedPreferences("credential_pref", Context.MODE_PRIVATE)
        val accessToken = prefs.getString("accessToken", null)
        val userId = prefs.getString("userId", null)

        saveBtn.setOnClickListener {

            val currentPass = currentPassEditText.text.toString()
            val newPass = newPassEditText.text.toString()
            val confirmPass = confirmPassEditText.text.toString()

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(requireContext(), "Data password harus dilengkapi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (accessToken != null && userId != null) {
                userViewModel.changePassword(accessToken, userId, currentPass, newPass, confirmPass) { success, message ->
                    if (success) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        val activity = view.context as FragmentActivity
                        val transaction = activity.supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frame_layout, ProfileFragment())
                        transaction.addToBackStack(null)
                        transaction.commit()
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Tombol kembali (Back)
        val imgBack = view.findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Tombol Cancel
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }
}
