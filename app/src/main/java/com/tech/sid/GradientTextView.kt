package com.tech.sid
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

class GradientTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Apply gradient shader to the text
        val paint = paint
        val width = w.toFloat()

        // Create a linear gradient from left to right
        val shader = LinearGradient(
            0f, height / 2f, // Start at left center
            width, height / 2f, // End at right center
            intArrayOf(
                0xFF9773FF.toInt(), // Start color: #9773FF
                0xFF00ACAC.toInt()  // End color: #00ACAC
            ),
            null,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
    }
}