package com.tech.sid


import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

class LoadingDotsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val dotSize = dpToPx(8)
    private val dotMargin = dpToPx(2)
    private val dotColor = Color.BLACK
    private val animationHeight = dpToPx(6).toFloat() // keep small bounce to stay inside
    private val animationDuration = 600L

    private val dot1 = createDot()
    private val dot2 = createDot()
    private val dot3 = createDot()

    init {
        // Make sure the parent view is tall enough to contain bouncing dots
        val minTotalHeight = dotSize + (animationHeight * 2).toInt()
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            minTotalHeight
        )

        // Avoid clipping if parent restricts children
        clipChildren = false
        clipToPadding = false

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            clipChildren = false
            clipToPadding = false

            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )

            setPadding(0, dpToPx(2), 0, dpToPx(2)) // vertical padding to absorb bounce

            addView(dot1)
            addView(dot2)
            addView(dot3)
        }

        addView(container)
        startBouncing()
    }

    private fun createDot(): View {
        val dot = View(context)
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(dotColor)
        }

        val params = LinearLayout.LayoutParams(dotSize, dotSize)
        params.setMargins(dotMargin, dotMargin, dotMargin, dotMargin)

        dot.layoutParams = params
        dot.background = drawable
        return dot
    }

    private fun startBouncing() {
        animateDot(dot1, 0)
        animateDot(dot2, 150)
        animateDot(dot3, 300)
    }

    private fun animateDot(dot: View, delay: Long) {
        ObjectAnimator.ofFloat(dot, "translationY", 0f, -animationHeight, 0f).apply {
            duration = animationDuration
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            startDelay = delay
            start()
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * context.resources.displayMetrics.density).toInt()
}
