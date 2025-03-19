package com.example.paku

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.example.paku.ui.viewmodel.UserViewModel

class VerificationOTPActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var otp1EditText: EditText
    private lateinit var otp2EditText: EditText
    private lateinit var otp3EditText: EditText
    private lateinit var otp4EditText: EditText
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verification_otp) // Sesuaikan dengan nama file XML OTP

        otp1EditText = findViewById(R.id.otp1)
        otp2EditText = findViewById(R.id.otp2)
        otp3EditText = findViewById(R.id.otp3)
        otp4EditText = findViewById(R.id.otp4)
        prefs = getSharedPreferences("credential_pref", MODE_PRIVATE)
        val email = prefs.getString("email", "").toString()

        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            val otp1 = otp1EditText.text.toString()
            val otp2 = otp2EditText.text.toString()
            val otp3 = otp3EditText.text.toString()
            val otp4 = otp4EditText.text.toString()

            if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty()) {
                Toast.makeText(this, "Kode OTP wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val otp = "$otp1$otp2$otp3$otp4"

            userViewModel.verifyOTP(email, otp) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success){
                    val intent = Intent(this, MakeNewPasswordActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}
