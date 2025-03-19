package com.example.paku

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.paku.ui.viewmodel.UserViewModel
import com.example.paku.utils.DeviceUtils
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var loginBtn: Button
    private lateinit var prefs: SharedPreferences
    private lateinit var rememberUsername: String
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        usernameEditText = findViewById(R.id.usernameLoginField)
        passwordEditText = findViewById(R.id.passwordLoginField)
        rememberMeCheckBox = findViewById(R.id.rememberMe)
        passwordLayout = findViewById(R.id.passwordLayout)
        loginBtn = findViewById(R.id.btnLogin)

        prefs = getSharedPreferences("credential_pref", MODE_PRIVATE)

        rememberUsername = prefs.getString("username", "").toString()
        usernameEditText.setText(rememberUsername)

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordEditText.hint = if (s.isNullOrEmpty()) "Password" else "" // Hilangkan hint saat mengetik
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Inisialisasi lupa password
        val forgotPasswordTextView = findViewById<TextView>(R.id.tvForgotPassword)
        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Inisialisasi tombol Login
        loginBtn.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val rememberMe = rememberMeCheckBox.isChecked
            val androidId = DeviceUtils.getAndroidID(this)

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Data diri harus dilengkapi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (rememberMe) {
                prefs.edit().putString("username", username).apply()
            }

            userViewModel.login(username, password, androidId) { success, message, loginData ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        prefs.edit()
                            .putString("accessToken", loginData?.accessToken)
                            .putString("refreshToken", loginData?.refreshToken)
                            .apply()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}