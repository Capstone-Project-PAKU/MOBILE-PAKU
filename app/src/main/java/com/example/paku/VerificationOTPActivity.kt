package com.example.paku

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class VerificationOTPActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verification_otp) // Sesuaikan dengan nama file XML OTP

        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            val intent = Intent(this, MakeNewPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
