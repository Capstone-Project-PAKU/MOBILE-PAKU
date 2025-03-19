package com.example.paku

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.paku.ui.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton

class MakeNewPasswordActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.make_new_password) // Sesuaikan dengan layout yang benar

        newPasswordEditText = findViewById(R.id.NewPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.ConfirmPasswordEditText)
        prefs = getSharedPreferences("credential_pref", MODE_PRIVATE)
        val email = prefs.getString("email", "").toString()

        val btnSave = findViewById<MaterialButton>(R.id.btnSave)
        btnSave.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.changePasswordWithOTP(email, newPassword, confirmPassword) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Menghapus history activity sebelumnya
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
