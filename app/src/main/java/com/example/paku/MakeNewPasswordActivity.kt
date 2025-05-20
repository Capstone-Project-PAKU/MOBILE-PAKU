package com.example.paku

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.paku.ui.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton

class MakeNewPasswordActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingOverlay: FrameLayout
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.make_new_password)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener { finish() }

        newPasswordEditText = findViewById(R.id.NewPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.ConfirmPasswordEditText)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        loadingOverlay = findViewById(R.id.loadingOverlay)

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

            // Tampilkan progress bar dan disable tombol Save supaya tidak bisa diklik berulang
            showLoading()

            userViewModel.changePasswordWithOTP(email, newPassword, confirmPassword) { success, message ->
                // Proses selesai, sembunyikan progress bar dan enable tombol
                hideLoading()

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        loadingOverlay.visibility = View.VISIBLE
        findViewById<MaterialButton>(R.id.btnSave).isEnabled = false
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        loadingOverlay.visibility = View.GONE
        findViewById<MaterialButton>(R.id.btnSave).isEnabled = true
    }
}

