package com.example.paku

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.paku.ui.viewmodel.UserViewModel

class VerificationOTPActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var otp1EditText: EditText
    private lateinit var otp2EditText: EditText
    private lateinit var otp3EditText: EditText
    private lateinit var otp4EditText: EditText
    private lateinit var resendTv: TextView
    private lateinit var timerTv: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userViewModel: UserViewModel by viewModels()
    private var timeLeftInMillis: Long = 5 * 60 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verification_otp)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener {
            finish()
        }

        otp1EditText = findViewById(R.id.otp1)
        otp2EditText = findViewById(R.id.otp2)
        otp3EditText = findViewById(R.id.otp3)
        otp4EditText = findViewById(R.id.otp4)
        resendTv = findViewById(R.id.tvResendCode)
        timerTv = findViewById(R.id.tvTimer)

        prefs = getSharedPreferences("credential_pref", MODE_PRIVATE)
        val email = prefs.getString("email", "").toString()

        startCountDown()
        resendTv.setOnClickListener {
            userViewModel.sendOTP(email) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    resetCountdown()
                }
            }
        }

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

    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                runOnUiThread {
                    Toast.makeText(this@VerificationOTPActivity, "Kode OTP sudah kadaluarsa", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun updateCountdownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        timerTv.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun resetCountdown() {
        countDownTimer.cancel()
        timeLeftInMillis = 5 * 60 * 1000
        updateCountdownText()
        startCountDown()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}
