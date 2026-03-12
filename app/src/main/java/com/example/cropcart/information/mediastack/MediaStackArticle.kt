package com.example.cropcart.information.mediastack

import retrofit2.http.GET
import retrofit2.http.Query

data class MediaStackResponse(
    val pagination: Pagination,
    val data: List<MediaStackArticle>
)

data class Pagination(val limit: Int, val offset: Int, val count: Int, val total: Int)

data class MediaStackArticle(
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val source: String,
    val image: String?,
    val category: String,
    val language: String,
    val country: String,
    val published_at: String
)

interface MediaStackService {
    @GET("v1/news")
    suspend fun getNews(
        @Query("access_key") apiKey: String,
        @Query("keywords") query: String? = null,
        @Query("languages") languages: String = "en",
        @Query("countries") countries: String = "in",
        @Query("limit") limit: Int = 25
    ): MediaStackResponse
}