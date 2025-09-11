package com.example.lokarasa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay then move to Login
        Handler().postDelayed({
            val intent =
                Intent(
                    this@Splash,
                    Login::class.java
                )
            startActivity(intent)
            finish() // close splash so user can't go back
        }, SPLASH_DURATION.toLong())
    }

    companion object {
        private const val SPLASH_DURATION = 2000 // 2 seconds
    }
}