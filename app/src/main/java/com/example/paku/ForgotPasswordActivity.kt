package com.example.paku

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import com.example.paku.ui.viewmodel.UserViewModel
import com.example.paku.utils.DeviceUtils
import com.google.android.material.button.MaterialButton

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var prefs: SharedPreferences
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingOverlay: FrameLayout
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener {
            finish()
        }

        emailEditText = findViewById(R.id.OTPEmailEditText)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        prefs = getSharedPreferences("credential_pref", MODE_PRIVATE)

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            val email = emailEditText.text.toString()
            val androidId = DeviceUtils.getAndroidID(this)

            if (email.isEmpty()) {
                Toast.makeText(this, "Email harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading()

            userViewModel.sendOTP(email, androidId) { success, message ->
                hideLoading()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    prefs.edit().putString("email", email).apply()
                    val intent = Intent(this, VerificationOTPActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        loadingOverlay.visibility = View.VISIBLE
        findViewById<MaterialButton>(R.id.btnContinue).isEnabled = false
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        loadingOverlay.visibility = View.GONE
        findViewById<MaterialButton>(R.id.btnContinue).isEnabled = true
    }
}
