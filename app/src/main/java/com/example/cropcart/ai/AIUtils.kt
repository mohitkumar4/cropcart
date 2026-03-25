package com.example.cropcart.ai

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.Duration

data class GeminiPart(val text: String)
data class GeminiContent(val parts: List<GeminiPart>)
data class GeminiRequest(val contents: List<GeminiContent>)

data class GeminiCandidate(val content: GeminiContent)
data class GeminiResponse(val candidates: List<GeminiCandidate>)

interface ApiService{
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun getChatResponse(
        @Query("key") api: String,
        @Body request: GeminiRequest,
    ): GeminiResponse
}

object AIRepo {
    private val geminiClient = OkHttpClient.Builder()
        .connectTimeout(Duration.ofSeconds(60))
        .readTimeout(Duration.ofSeconds(60))
        .writeTimeout(Duration.ofSeconds(60))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(geminiClient)
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    enum class MessageStatus(val value: Int) {
        NORMAL(0),
        ERROR(1),
    }
}