package com.example.cropcart.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cropcart.BuildConfig
import com.example.cropcart.R
import com.example.cropcart.gui.text.SimpleTextView
import com.example.cropcart.information.openweather.OpenWeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherFragment : Fragment() {
    // views
    private lateinit var frg: View
    private lateinit var titleV: SimpleTextView
    private lateinit var temperatureV: SimpleTextView
    private lateinit var humidityV: SimpleTextView
    private lateinit var descV: SimpleTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        frg = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_weather, container, false)

        titleV = frg.findViewById(R.id.title)
        temperatureV = frg.findViewById(R.id.temperature)
        humidityV = frg.findViewById(R.id.humidity)
        descV = frg.findViewById(R.id.desc)

        lifecycleScope.launch { getWeatherUpdate() }

        return frg
    }


    private suspend fun getWeatherUpdate() {
        val openWeatherApiKey = BuildConfig.OPENWEATHER_API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(OpenWeatherService::class.java)

        try {
            val response = withContext(Dispatchers.IO) {
                service.getCurrentWeather("Phagwara", openWeatherApiKey)
            }

            withContext(Dispatchers.Main) {
                titleV.text = response.name
                temperatureV.text = "${response.main.temp.toInt()}°C"
                humidityV.text = "${response.main.humidity} g/m³"
                descV.text = response.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "N/A"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}