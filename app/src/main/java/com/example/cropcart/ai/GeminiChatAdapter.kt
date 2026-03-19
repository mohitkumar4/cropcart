package com.example.cropcart.ai

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.example.cropcart.gui.text.SimpleTextView

class GeminiChatAdapter(private val context: Context) : RecyclerView.Adapter<GeminiChatAdapter.ChatViewHolder>() {
    private var chatMessages: MutableList<AIChatMessage> = mutableListOf()

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val msgTv: SimpleTextView = view.findViewById<SimpleTextView>(R.id.msg)
        private val icon: ImageView = view.findViewById<ImageView>(R.id.icon)
        private val linearLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.aiChatLinearLayout)

        fun bind(item: AIChatMessage){
            msgTv.text = item.msg

            icon.setImageDrawable(context.resources.getDrawable(if(item.isUser) R.drawable.person else R.drawable.gemini_logo))
            linearLayout.setLayoutDirection(if(item.isUser) View.LAYOUT_DIRECTION_LTR else View.LAYOUT_DIRECTION_RTL)
        }
    }

    fun addMessage(item: AIChatMessage){
        chatMessages.add(item)
        notifyItemInserted(chatMessages.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        return ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_ai_chat_message, parent, false))
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {
        holder.bind(chatMessages[position])
    }

    override fun getItemCount(): Int = chatMessages.size
}