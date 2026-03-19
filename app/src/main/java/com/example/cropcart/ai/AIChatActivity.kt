package com.example.cropcart.ai

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.BuildConfig
import com.example.cropcart.R
import com.example.cropcart.ai.AIRepo.MessageStatus
import com.example.cropcart.gui.text.GuiRepo.setTiledBackground
import com.example.cropcart.gui.text.SimpleTextView
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

class AIChatActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var inputField: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var adapter: GeminiChatAdapter
    private lateinit var emptyChatCtn: LinearLayout
    private lateinit var cardViewSendBtn: CardView

    private val geminiAPIKey: String = BuildConfig.GEMINI_API

    private lateinit var enabledColorSendBtn: ColorStateList
    private lateinit var disabledColorSendBtn: ColorStateList

    // state tracker
    private var isWaitingForResponse: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_chat)
        findViewById<LinearLayout>(R.id.aiChatInterfaceMainLayout).setTiledBackground(R.drawable.pattern_topography, 0.25f, 0.05f)

        val welcomeLogo = findViewById<ImageView>(R.id.welcomeLogo)
        val welcomeText = findViewById<SimpleTextView>(R.id.welcomeText)
        welcomeLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        welcomeText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        enabledColorSendBtn = resources.getColorStateList(R.color.primary_lighter, theme)
        disabledColorSendBtn = resources.getColorStateList(R.color.gray, theme)

        inputField = findViewById<EditText>(R.id.aiCharInputField)
        btnSend = findViewById<ImageButton>(R.id.btnSend)
        emptyChatCtn = findViewById<LinearLayout>(R.id.emptyChatCtn)
        cardViewSendBtn = findViewById<CardView>(R.id.cardViewSendBtn)

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
        setWaitingStateTo(true)
        if (rv.visibility != View.VISIBLE){
            rv.visibility = View.VISIBLE
            emptyChatCtn.visibility = View.GONE
        }

        scrollToBottom()

        val loadingMessage = AIChatMessage(false, "_Thinking..._", isBlinking=true)
        adapter.addMessage(loadingMessage)
        val loadingPosition = adapter.itemCount - 1

        lifecycleScope.launch{
            try {
                val request = GeminiRequest(listOf(GeminiContent(listOf(GeminiPart(userText)))))
                val response = AIRepo.apiService.getChatResponse(geminiAPIKey, request)
                val aiReply = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "No response from AI"

                adapter.updateMessage(loadingPosition, aiReply, isBlinking=false)
                scrollToBottom()
            }
            catch (e: HttpException){
                val errMessage = when(e.code()){
                    429 -> "You have exceeded your API usage"
                    else -> "${e.code()}: ${e.message}"
                }
                adapter.updateMessage(loadingPosition, "Error: ${errMessage}", isBlinking=false, status=MessageStatus.ERROR)
            }
            catch (e: UnknownHostException){
                adapter.updateMessage(loadingPosition, "Error: can't connect to the server\nYour internet may be off", isBlinking=false, status=MessageStatus.ERROR)
            }
            catch (e: Exception) {
                adapter.updateMessage(loadingPosition, "Error: ${e.localizedMessage}", isBlinking=false, status=MessageStatus.ERROR)
                e.printStackTrace()
            }
            finally{
                setWaitingStateTo(false)
            }
        }
    }

    private fun scrollToBottom(){
        rv.scrollToPosition(adapter.itemCount - 1)
    }

    private fun setWaitingStateTo(state: Boolean){
        isWaitingForResponse = state
        inputField.isEnabled = !state
        btnSend.isEnabled = !state

        cardViewSendBtn.setCardBackgroundColor(if(state) disabledColorSendBtn else enabledColorSendBtn)
        inputField.backgroundTintList = if(state) disabledColorSendBtn else enabledColorSendBtn
    }
}