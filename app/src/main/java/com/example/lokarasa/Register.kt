package com.example.lokarasa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        val emailEditText = findViewById<TextInputEditText>(R.id.EmailRegister)
        val passwordEditText = findViewById<TextInputEditText>(R.id.PasswordRegister)

        val registerButton = findViewById<Button>(R.id.buttonSignUp)
        val GoToLoginPage = findViewById<TextView>(R.id.textViewLogin)

        registerButton.setOnClickListener {
            val email = emailEditText.text?.toString()?.trim() ?: ""
            val password = passwordEditText.text?.toString()?.trim() ?: ""

            if (email.isNotEmpty() && password.length >= 6) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter valid email & password (6+ chars)", Toast.LENGTH_SHORT).show()
            }
        }

        GoToLoginPage.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }
}
