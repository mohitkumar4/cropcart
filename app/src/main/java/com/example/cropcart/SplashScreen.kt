package com.example.cropcart

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        Handler(Looper.getMainLooper()).postDelayed({

            if(isLoggedIn())
            {
                startActivity(Intent(this, MainActivity::class.java))
            }
            else
            {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            finish()
        }, 2000)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun isLoggedIn(): Boolean {

        val currentAuth = FirebaseAuth.getInstance().currentUser

        if(currentAuth != null)
        {
            return true
        }
        else
        {
            return false
        }

    }

}