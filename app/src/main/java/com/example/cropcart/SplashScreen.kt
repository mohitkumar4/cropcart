package com.example.cropcart

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
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

        val imageView: ImageView = findViewById(R.id.imageView)

        val fadeIn = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f, 1f)

        val scaleX = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 0.8f, 1f)

        val slideUp = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, 50f, 0f)

        val animatorSet = AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY, slideUp)
            duration = 1000

            interpolator = OvershootInterpolator(1.2f)
            start()
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Handler(Looper.getMainLooper()).postDelayed({
            val activity = if (isLoggedIn()) AccountTypeSelection::class.java else LoginActivity::class.java
            startActivity(Intent(this, activity))
            finish()
        }, 2000)
    }

    private fun isLoggedIn(): Boolean = FirebaseAuth.getInstance().currentUser != null
}