package com.example.cropcart.ai

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.BuildConfig
import com.example.cropcart.R
import com.example.cropcart.gui.text.GuiRepo.setTiledBackground
import com.example.cropcart.gui.text.SimpleTextView
import kotlinx.coroutines.launch

class AIChatActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var inputField: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var adapter: GeminiChatAdapter
    private lateinit var emptyChatCtn: LinearLayout

    private val geminiAPIKey: String = BuildConfig.GEMINI_API

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_chat)
        findViewById<LinearLayout>(R.id.aiChatInterfaceMainLayout).setTiledBackground(R.drawable.pattern_topography, 0.25f, 0.1f)

        val welcomeLogo = findViewById<ImageView>(R.id.welcomeLogo)
        val welcomeText = findViewById<SimpleTextView>(R.id.welcomeText)
        welcomeLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        welcomeText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

        inputField = findViewById<EditText>(R.id.aiCharInputField)
        btnSend = findViewById<ImageButton>(R.id.btnSend)
        emptyChatCtn = findViewById<LinearLayout>(R.id.emptyChatCtn)

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


        val loadingPosition = adapter.itemCount - 1
        adapter.addMessage(AIChatMessage(true, userText))
        if (rv.visibility != View.VISIBLE){
            rv.visibility = View.VISIBLE
            emptyChatCtn.visibility = View.GONE
        }

        scrollToBottom()

        val loadingMessage = AIChatMessage(false, "_Thinking..._")
        adapter.addMessage(loadingMessage)
        scrollToBottom()

        lifecycleScope.launch{
            try {
                val request = GeminiRequest(listOf(GeminiContent(listOf(GeminiPart(userText)))))
                val response = AIRepo.apiService.getChatResponse(geminiAPIKey, request)
                val aiReply = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "No response from AI"

                adapter.updateMessage(loadingPosition, aiReply)
                scrollToBottom()
            } catch (e: Exception) {
                Toast.makeText(this@AIChatActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scrollToBottom(){
        rv.scrollToPosition(adapter.itemCount - 1)
    }
}