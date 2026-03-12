package com.example.cropcart.information

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.cropcart.R

class InformationActivity : AppCompatActivity() {
    private lateinit var btnNews: ImageButton
    private lateinit var btnWeather: ImageButton
    private lateinit var frgMng: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        btnNews = findViewById(R.id.btnNews)
        btnWeather = findViewById(R.id.btnWeather)
        frgMng = supportFragmentManager


        if (savedInstanceState == null) {
            setFragment(NewsFragment())
        }

        btnNews.setOnClickListener{ setFragment(NewsFragment()) }
        btnWeather.setOnClickListener{ setFragment(WeatherFragment()) }
    }

    private fun setFragment(fragment: Fragment) {
        frgMng.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

        updateButtonPadding(fragment)
    }


    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun updateButtonPadding(fragment: Fragment?) {
        val selected = 24.dpToPx()
        val unselected = 16.dpToPx()


        when (fragment) {
            is NewsFragment -> {
                btnNews.setPadding(selected, selected, selected, selected)
                btnWeather.setPadding(unselected, unselected, unselected, unselected)
            }
            is WeatherFragment -> {
                btnWeather.setPadding(selected, selected, selected, selected)
                btnNews.setPadding(unselected, unselected, unselected, unselected)
            }
        }
    }
}