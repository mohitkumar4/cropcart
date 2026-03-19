package com.example.cropcart.gui.text

import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Matrix
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.View

object GuiRepo {
    fun View.setTiledBackground(drawableId: Int, scale: Float = 1.0f, alpha: Float = 1.0f) {
        val bitmap = BitmapFactory.decodeResource(resources, drawableId)
        val shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

        val matrix = Matrix()
        matrix.postScale(scale, scale)
        shader.setLocalMatrix(matrix)

        val paintDrawable = ShapeDrawable(RectShape())

        paintDrawable.paint.shader = shader
        val clampedAlpha = alpha.coerceIn(0f, 1f)
        paintDrawable.paint.alpha = (clampedAlpha * 255).toInt()
        this.background = paintDrawable
    }
}