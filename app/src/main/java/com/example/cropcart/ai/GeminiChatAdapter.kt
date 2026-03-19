package com.example.cropcart.ai

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
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
    private val textColorNormal = context.resources.getColor(R.color.black, context.theme)
    private val textColorError = context.resources.getColor(R.color.status_error, context.theme)

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val msgTv: SimpleTextView = view.findViewById<SimpleTextView>(R.id.msg)
        private val icon: ImageView = view.findViewById<ImageView>(R.id.icon)
        private val linearLayout: ConstraintLayout = view.findViewById<ConstraintLayout>(R.id.aiChatContentLayout)
        private val aiChatItemCard: CardView = view.findViewById<CardView>(R.id.aiChatItemCard)

        private val aiChatWrapperLinearLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.aiChatWrapperLinearLayout)

        fun bind(item: AIChatMessage){
            if(item.isBlinking) {
                msgTv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.blink))
                icon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.quick_fade_in))
            }
            else {
                msgTv.clearAnimation()
                icon.clearAnimation()
            }

            if(item.status == AIRepo.MessageStatus.ERROR){
                msgTv.setTextColor(textColorError)
            } else {
                msgTv.setTextColor(textColorNormal)
            }

            icon.setImageDrawable(if(item.isUser) iconUser else iconGemini)
            linearLayout.setLayoutDirection(if(item.isUser) View.LAYOUT_DIRECTION_LTR else View.LAYOUT_DIRECTION_RTL)

            aiChatWrapperLinearLayout.setLayoutDirection(if(item.isUser) View.LAYOUT_DIRECTION_LTR else View.LAYOUT_DIRECTION_RTL)

            markwon.setMarkdown(msgTv, item.msg.replace("\n", "  \n"))
            msgTv.movementMethod = LinkMovementMethod.getInstance()

            icon.visibility = if(item.isUser) View.GONE else View.VISIBLE
            aiChatItemCard.setCardBackgroundColor(if(item.isUser) backgroundColorUser else backgroundColorGemini)
        }
    }

    fun addMessage(item: AIChatMessage){
        chatMessages.add(item)
        notifyItemInserted(chatMessages.size - 1)
    }

    fun updateMessage(position: Int, newText: String, isBlinking: Boolean = false, status: AIRepo.MessageStatus=AIRepo.MessageStatus.NORMAL) {
        if (position >= 0 && position < chatMessages.size) {
            val message = chatMessages[position]
            message.msg = newText
            message.isBlinking = isBlinking
            message.status = status

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
