package com.example.paku

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.paku.ui.viewmodel.UserViewModel
import com.example.paku.utils.DeviceUtils

class RegisterActivity : AppCompatActivity() {
    private lateinit var employeeIdEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var androidIdEditText: EditText
    private lateinit var registerBtn: Button
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        employeeIdEditText = findViewById(R.id.EmployeeIdField)
        usernameEditText = findViewById(R.id.usernameField)
        passwordEditText = findViewById(R.id.passwordField)
        androidIdEditText = findViewById(R.id.AndroidIdField)
        registerBtn = findViewById(R.id.btnRegister)
        showAndoridID()

        registerBtn.setOnClickListener {
            val employeeID = employeeIdEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val androidId = androidIdEditText.text.toString()
            val role = "user"

            if (employeeID.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Data diri harus dilengkapi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.register(employeeID, username, password, role, androidId) { success, message ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (success) {
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

    private fun showAndoridID() {
        val androidID = DeviceUtils.getAndroidID(this)
        androidIdEditText.setText(androidID)
    }
}
