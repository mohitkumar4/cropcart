package com.example.cropcart.news.apitube

import retrofit2.http.GET
import retrofit2.http.Query

data class Source(val name: String)

data class ApitubeArticle(
val title: String,
val description: String?,
val published_at: String,
val source: Source,
)

data class NewsResponse(
    val status: String,
    val results: List<ApitubeArticle>
)

interface ApiTubeService {
    @GET("v1/news/everything")
    suspend fun getEverything(
        @Query("api_key") apiKey: String,
        @Query("q") query: String? = null,
        @Query("language.code") language: String = "en",
        @Query("source.country.code") country: String = "in",
        @Query("per_page") perPage: Int = 10
    ): NewsResponse
}