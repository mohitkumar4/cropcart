package com.example.cropcart.information

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.cropcart.R
import com.example.cropcart.gui.text.GuiRepo.setTiledBackground

class InformationActivity : AppCompatActivity() {
    private lateinit var btnNews: ImageButton
    private lateinit var btnWeather: ImageButton
    private lateinit var frgMng: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        findViewById<LinearLayout>(R.id.mainInformationLayout).setTiledBackground(R.drawable.pattern_topography, 0.25f, 0.05f)

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