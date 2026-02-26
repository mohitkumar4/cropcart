package com.example.cropcart

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object GeminiSearchHelper {

    private const val API_KEY = "AIzaSyCrahoGiP8J_RdRNeA3Tq0VWlDcXQ76FjI" // ⚠️ Replace this with your actual key

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = API_KEY
    )

    suspend fun parseSearchQuery(query: String): Map<String, Any?> = withContext(Dispatchers.IO) {
        val prompt = """
            You are a natural language parser for a grocery shopping app.
            Convert user queries into JSON filters with the following fields:
            - category (like fruits, vegetables, nuts, grains)
            - section (like organic, dried, processed, hybrid)
            - price_min (number)
            - price_max (number)
            - keywords (string)
            
            Only return JSON, no explanation.
            Examples:
            - "show me organic fruits under 100" ->
              {"category":"fruits","section":"organic","price_max":100}
            - "vegetables above 50" ->
              {"category":"vegetables","price_min":50}
            - "dried mango" ->
              {"section":"dried","keywords":"mango"}
        """.trimIndent()

        val response = model.generateContent("$prompt\nUser: $query")

        val text = response.text ?: "{}"
        return@withContext parseJsonToMap(text)
    }

    private fun parseJsonToMap(json: String): Map<String, Any?> {
        return try {
            val cleanJson = json.trim().removePrefix("```json").removeSuffix("```").trim()
            val jsonObject = JSONObject(cleanJson)
            val map = mutableMapOf<String, Any?>()

            jsonObject.keys().forEach { key ->
                map[key] = jsonObject.opt(key)
            }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }
}