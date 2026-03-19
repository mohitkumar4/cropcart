package com.example.cropcart.ai

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

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
