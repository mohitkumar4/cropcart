package com.example.cropcart.prompt

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.example.cropcart.R
import com.example.cropcart.gui.text.SimpleTextView

fun <T> MutableCollection<T>.toggle(item: T) : Boolean {
    val isIn: Boolean = contains(item)
    if (isIn) remove(item) else add(item)
    return !isIn
}

class PromptAIAttachment(
    context: Context,
    selection: MutableSet<PromptRepo.AIAttachment> = mutableSetOf(),
    result: (MutableSet<PromptRepo.AIAttachment>, Boolean) -> Unit = {_, _ -> }
) : Dialog(context) {
    private var view = LayoutInflater.from(context).inflate(R.layout.prompt_ai_attachment, null, false)
    private var promptCard: CardView = view.findViewById<CardView>(R.id.promptCard)
    private var titleV: SimpleTextView = view.findViewById<SimpleTextView>(R.id.title)
    private var cartV: ImageButton = view.findViewById<ImageButton>(R.id.btnCart)
    private var cartCheckMark: ImageView = view.findViewById<ImageView>(R.id.cartCheckMark)
    private var btnConfirm: Button = view.findViewById<Button>(R.id.btnConfirm)

    // state trackers
    private var userSubmitted: Boolean = false

    init{
        setSelectionStateBaseOnCondition(selection.contains(PromptRepo.AIAttachment.CART))

        setContentView(view)

        titleV.text = "Select information that you want Gemini to know"

        cartV.setOnClickListener {
            setSelectionStateBaseOnCondition(selection.toggle(PromptRepo.AIAttachment.CART))
        }

        btnConfirm.setOnClickListener {
            userSubmitted = true
            dismiss()
        }

        setOnDismissListener {
            result(selection, userSubmitted)
        }

        show()
    }

    private fun setSelectionStateBaseOnCondition(condition: Boolean){
        cartCheckMark.visibility = if(condition) View.VISIBLE else View.GONE
    }
}