package com.example.cropcart.ai

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.BuildConfig
import com.example.cropcart.R
import kotlinx.coroutines.launch

class AIChatActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var inputField: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var adapter: GeminiChatAdapter

    private val geminiAPIKey: String = BuildConfig.GEMINI_API

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_chat)

        inputField = findViewById<EditText>(R.id.aiCharInputField)
        btnSend = findViewById<ImageButton>(R.id.btnSend)

        rv = findViewById(R.id.rvAIChat)
        adapter = GeminiChatAdapter(this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        btnSend.setOnClickListener {
            evaluateInput()
        }
    }

    private fun evaluateInput(){
        val userText = inputField.text.toString().trim()
        if(userText.isEmpty()){
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        inputField.setText("")

        adapter.addMessage(AIChatMessage(true, userText))

        val loadingMessage = AIChatMessage(false, "_Thinking..._")
        adapter.addMessage(loadingMessage)
        val loadingPosition = adapter.itemCount - 1

        lifecycleScope.launch{
            try {
                val request = GeminiRequest(listOf(GeminiContent(listOf(GeminiPart(userText)))))
                val response = AIRepo.apiService.getChatResponse(geminiAPIKey, request)
                val aiReply = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "No response from AI"

                adapter.updateMessage(loadingPosition, aiReply)
            } catch (e: Exception) {
                Toast.makeText(this@AIChatActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}