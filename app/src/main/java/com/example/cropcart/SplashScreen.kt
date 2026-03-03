package com.example.cropcart

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cropcart.account.AccountTypeSelection
import com.example.cropcart.account.LoginActivity
import com.example.cropcart.firebase.FirebaseRepo
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        val appNameText: TextView = findViewById<TextView>(R.id.appName)
        appNameText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Handler(Looper.getMainLooper()).postDelayed({
            val activity = if (isLoggedIn()) AccountTypeSelection::class.java else LoginActivity::class.java
            startActivity(Intent(this, activity))
            finish()
        }, 2000)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun isLoggedIn(): Boolean = FirebaseAuth.getInstance().currentUser != null
}