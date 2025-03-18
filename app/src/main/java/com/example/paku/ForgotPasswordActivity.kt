package com.example.paku

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ForgotPasswordActivity : AppCompatActivity() { // Sesuaikan dengan activity tempat tombol ini berada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password) // Sesuaikan dengan file XML yang sesuai

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            val intent = Intent(this, VerificationOTPActivity::class.java)
            startActivity(intent)
        }
    }
}
