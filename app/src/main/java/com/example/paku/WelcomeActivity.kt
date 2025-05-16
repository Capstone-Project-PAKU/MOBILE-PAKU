package com.example.paku

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)

        // Inisialisasi tombol Sign In
        findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Setup TextView dengan SpannableString
        val textViewRegister = findViewById<TextView>(R.id.desc_Register)
        val fullText = "Belum memiliki akun? Silahkan Register"
        val spannableString = SpannableString(fullText)

        // Menentukan posisi kata "Register" dalam teks
        val startIndex = fullText.indexOf("Register")
        val endIndex = startIndex + "Register".length

        // Membuat teks "Register" bisa diklik
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Berpindah ke RegisterActivity saat diklik
                val intent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // Menghilangkan garis bawah default
            }
        }

        // Terapkan style pada kata "Register"
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.dark_blue)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textViewRegister.text = spannableString
        textViewRegister.movementMethod = LinkMovementMethod.getInstance() // Agar teks bisa diklik
    }
}
