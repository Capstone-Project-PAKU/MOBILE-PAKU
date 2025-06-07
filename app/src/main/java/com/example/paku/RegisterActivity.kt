package com.example.paku

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.paku.ui.viewmodel.UserViewModel
import com.example.paku.utils.DeviceUtils
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {
    private lateinit var employeeIdEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var registerBtn: Button
    private lateinit var prefs: SharedPreferences
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingOverlay: FrameLayout
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        employeeIdEditText = findViewById(R.id.EmployeeIdField)
        usernameEditText = findViewById(R.id.usernameField)
        passwordEditText = findViewById(R.id.passwordField)
        emailEditText = findViewById(R.id.emailField)
        registerBtn = findViewById(R.id.btnRegister)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        loadingOverlay = findViewById(R.id.loadingOverlay)

        prefs = getSharedPreferences("credential_pref", MODE_PRIVATE)
        registerBtn.setOnClickListener {
            val employeeID = employeeIdEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val email = emailEditText.text.toString()
            val androidId = DeviceUtils.getAndroidID(this)
            val role = "user"

            if (employeeID.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Data diri harus dilengkapi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.register(employeeID, username, email, password, role, androidId) { success, message, registerData ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        prefs.edit().putString("userId", registerData?.id_user).apply()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }

        val imgBack = findViewById<ImageView>(R.id.back)
        imgBack.setOnClickListener {
            finish()
        }

        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            finish()
        }
    }
    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        loadingOverlay.visibility = View.VISIBLE
        findViewById<MaterialButton>(R.id.btnRegister).isEnabled = false
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        loadingOverlay.visibility = View.GONE
        findViewById<MaterialButton>(R.id.btnRegister).isEnabled = true
    }
}
