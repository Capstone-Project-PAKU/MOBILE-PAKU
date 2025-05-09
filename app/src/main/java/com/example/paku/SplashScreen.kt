package com.example.paku

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.paku.ui.viewmodel.UserViewModel
import com.example.paku.utils.TokenManager
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var tokenManager: TokenManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        tokenManager = TokenManager(applicationContext)

        val accessToken = tokenManager.getAccessToken()
        val refreshToken = tokenManager.getRefreshToken()
        if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
            checkTokenValidity()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }, 1000)
        }

        // Animasi fade in untuk logo
        val logo: ImageView = findViewById(R.id.logo)
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logo.startAnimation(animation)
    }

    private fun checkTokenValidity() {
        lifecycleScope.launch {
            val newAccessToken = tokenManager.refreshToken()
            if (newAccessToken != null) {
                userViewModel.validateToken { isValid ->
                    runOnUiThread {
                        if (isValid) {
                            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                            finish()
                        }
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@SplashScreen, "Sesi sudah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SplashScreen, WelcomeActivity::class.java))
                    finish()
                }
            }
        }
    }
}
