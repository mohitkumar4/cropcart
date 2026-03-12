package com.example.cropcart.information.openweather

import retrofit2.http.GET
import retrofit2.http.Query

data class CurrentWeatherResponse(
    val name: String,
    val main: MainTemp,
    val weather: List<WeatherDescription>,
    val dt: Long
)

data class MainTemp(val temp: Double, val humidity: Int)
data class WeatherDescription(val description: String, val icon: String)

interface OpenWeatherService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): CurrentWeatherResponse
}