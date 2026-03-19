package com.example.cropcart.ai

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R

class AIChatActivity : AppCompatActivity(), ApiService {
    private lateinit var rv: RecyclerView
    private lateinit var inputField: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var adapter: GeminiChatAdapter

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
        val newMessageToAI = inputField.text.toString().trim()
        if(newMessageToAI.isEmpty()){
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        inputField.setText("")

        adapter.addMessage(AIChatMessage(true, newMessageToAI))
        adapter.addMessage(AIChatMessage(false, "Mock reply"))
    }

    override suspend fun getChatResponse(
        api: String,
        request: GeminiRequest
    ): GeminiResponse {
        TODO("Not yet implemented")
    }
}