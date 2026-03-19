package com.example.cropcart.ai

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.example.cropcart.gui.text.SimpleTextView
import io.noties.markwon.Markwon

class GeminiChatAdapter(private val context: Context) : RecyclerView.Adapter<GeminiChatAdapter.ChatViewHolder>() {
    private var chatMessages: MutableList<AIChatMessage> = mutableListOf()
    private val markwon = Markwon.create(context)
    private val iconGemini = context.resources.getDrawable(R.drawable.gemini_logo, context.theme)
    private val iconUser = context.resources.getDrawable(R.drawable.person, context.theme)
    private val backgroundColorUser = context.resources.getColor(R.color.background_lighter, context.theme)
    private val backgroundColorGemini = context.resources.getColor(R.color.white, context.theme)

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val msgTv: SimpleTextView = view.findViewById<SimpleTextView>(R.id.msg)
        private val icon: ImageView = view.findViewById<ImageView>(R.id.icon)
        private val linearLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.aiChatContentLinearLayout)
        private val aiChatItemCard: CardView = view.findViewById<CardView>(R.id.aiChatItemCard)
        private val aiChatWrapperLinearLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.aiChatWrapperLinearLayout)

        fun bind(item: AIChatMessage){
            icon.setImageDrawable(if(item.isUser) iconUser else iconGemini)
            linearLayout.setLayoutDirection(if(item.isUser) View.LAYOUT_DIRECTION_LTR else View.LAYOUT_DIRECTION_RTL)
            aiChatWrapperLinearLayout.setLayoutDirection(if(item.isUser) View.LAYOUT_DIRECTION_LTR else View.LAYOUT_DIRECTION_RTL)

            val markdown = item.msg
            markwon.setMarkdown(msgTv, markdown)
            msgTv.movementMethod = LinkMovementMethod.getInstance()

            icon.visibility = if(item.isUser) View.GONE else View.VISIBLE
            aiChatItemCard.setCardBackgroundColor(if(item.isUser) backgroundColorUser else backgroundColorGemini)
        }
    }

    fun addMessage(item: AIChatMessage){
        chatMessages.add(item)
        notifyItemInserted(chatMessages.size - 1)
    }

    fun updateMessage(position: Int, newText: String) {
        if (position >= 0 && position < chatMessages.size) {
            val message = chatMessages[position]
            message.msg = newText

            notifyItemChanged(position)
        }
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