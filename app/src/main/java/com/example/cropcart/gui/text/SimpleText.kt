package com.example.cropcart.gui.text

import android.content.Context
import android.util.AttributeSet
import android.graphics.Typeface
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import com.example.cropcart.R

class SimpleTextView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = android.R.attr.textViewStyle
): AppCompatTextView(context, attrs, defStyle){
    init{
        setTextColor(context.getColor(R.color.text_base))

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.SimpleTextView, 0, 0)
            val preset = typedArray.getInt(R.styleable.SimpleTextView_preset, -1)
            typedArray.recycle()

            when (preset){
                1 -> {
                    setTextSize( TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.activity_name) )
                    setTypeface(typeface, Typeface.BOLD)
                }
                2 -> {
                    setTextSize( TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.content_primary_text) )
                    setTypeface(typeface, Typeface.BOLD)
                }
                3 -> {
                    setTextSize( TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.content_secondary_text) )
                }
            }
        }
    }
}