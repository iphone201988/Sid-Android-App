package com.tech.sid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

class GradientView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val fillPaint = Paint()
    private val strokePaint = Paint()
    private var gradientColors: IntArray = intArrayOf(Color.RED, Color.BLUE)
    private var fillColor: Int = Color.TRANSPARENT // Default fill color
    private val strokeWidthPx: Float
    private val cornerRadiusPx: Float

    init {
        // Convert 2dp to pixels for stroke width
        strokeWidthPx = 1f * context.resources.displayMetrics.density
        // Convert 20dp to pixels for corner radius
        cornerRadiusPx = context.resources.getDimension(com.intuit.sdp.R.dimen._20sdp)

        // Initialize paints
        fillPaint.style = Paint.Style.FILL
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidthPx

        // Load attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.GradientViewSubscription)
            val startColor = typedArray.getColor(R.styleable.GradientViewSubscription_startColor, Color.RED)
            val endColor = typedArray.getColor(R.styleable.GradientViewSubscription_endColor, Color.BLUE)
            fillColor = typedArray.getColor(R.styleable.GradientViewSubscription_fillColor24, Color.TRANSPARENT) // Fixed typo
            gradientColors = intArrayOf(startColor, endColor)
            typedArray.recycle()
        }

        setWillNotDraw(false) // Enable custom drawing
    }

    // Setter for startColor (for data binding)
    fun setStartColor(startColor: Int) {
        gradientColors = intArrayOf(startColor, gradientColors.getOrElse(1) { Color.BLUE })
        invalidate()
    }

    // Setter for endColor (for data binding)
    fun setEndColor(endColor: Int) {
        gradientColors = intArrayOf(gradientColors.getOrElse(0) { Color.RED }, endColor)
        invalidate()
    }

    // Setter for gradient colors (for programmatic use)
    fun setGradientColors(colors: IntArray) {
        gradientColors = colors
        invalidate()
    }

    // Setter for fill color (for programmatic use and data binding)
    fun setFillColor(color: Int) {
        fillColor = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Set up gradient for stroke
        val shader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            gradientColors,
            null,
            Shader.TileMode.CLAMP
        )
        strokePaint.shader = shader

        // Set fill color
        fillPaint.color = fillColor

        // Define rectangle for drawing, accounting for stroke width
        val rect = RectF(
            strokeWidthPx / 2,
            strokeWidthPx / 2,
            width.toFloat() - strokeWidthPx / 2,
            height.toFloat() - strokeWidthPx / 2
        )

        // Draw filled rounded rectangle
        canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, fillPaint)

        // Draw stroked rounded rectangle
        canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, strokePaint)
    }
}
//package com.tech.sid
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.LinearGradient
//import android.graphics.Paint
//import android.graphics.RectF
//import android.graphics.Shader
//import android.util.AttributeSet
//import android.widget.LinearLayout
//import androidx.core.content.ContextCompat
//import kotlin.math.min
//
//class GradientView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null
//) : LinearLayout(context, attrs) {
//
//    private val fillPaint = Paint()
//    private val strokePaint = Paint()
//    private var gradientColors: IntArray = intArrayOf(Color.RED, Color.BLUE)
//    private var fillColor: Int = Color.TRANSPARENT // Default fill color
//    private val strokeWidthPx: Float
//
//    init {
//        // Convert 2dp to pixels
//        strokeWidthPx = 2f * context.resources.displayMetrics.density
//
//        // Initialize paints
//        fillPaint.style = Paint.Style.FILL
//        strokePaint.style = Paint.Style.STROKE
//        strokePaint.strokeWidth = strokeWidthPx
//
//        // Load attributes
//        attrs?.let {
//            val typedArray = context.obtainStyledAttributes(it, R.styleable.GradientViewSubscription)
//            val startColor = typedArray.getColor(R.styleable.GradientViewSubscription_startColor, Color.RED)
//            val endColor = typedArray.getColor(R.styleable.GradientViewSubscription_endColor, Color.BLUE)
//            gradientColors = intArrayOf(startColor, endColor)
//            fillColor = typedArray.getColor(R.styleable.GradientViewSubscription_fillColor24, Color.TRANSPARENT)
//            typedArray.recycle()
//        }
//
//        setWillNotDraw(false) // Enable custom drawing
//    }
//
//    // Setter for startColor (for data binding)
//    fun setStartColor(startColor: Int) {
//        gradientColors = intArrayOf(startColor, gradientColors.getOrElse(1) { Color.BLUE })
//        invalidate()
//    }
//
//    // Setter for endColor (for data binding)
//    fun setEndColor(endColor: Int) {
//        gradientColors = intArrayOf(gradientColors.getOrElse(0) { Color.RED }, endColor)
//        invalidate()
//    }
//
//    // Setter for gradient colors (for programmatic use)
//    fun setGradientColors(colors: IntArray) {
//        gradientColors = colors
//        invalidate()
//    }
//
//    // Setter for fill color (for programmatic use and data binding)
//    fun setFillColor(color: Int) {
//        fillColor = color
//        invalidate()
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        // Set up gradient for stroke
//        val shader = LinearGradient(
//            0f, 0f, 0f, height.toFloat(),
//            gradientColors,
//            null,
//            Shader.TileMode.CLAMP
//        )
//        strokePaint.shader = shader
//
//        // Set fill color
//        fillPaint.color = fillColor
//
//        // Define rectangle for drawing, accounting for stroke width
//        val rect = RectF(
//            strokeWidthPx / 2,
//            strokeWidthPx / 2,
//            width.toFloat() - strokeWidthPx / 2,
//            height.toFloat() - strokeWidthPx / 2
//        )
//
//        // Draw filled rectangle
//        canvas.drawRect(rect, fillPaint)
//
//        // Draw stroked rectangle
//        canvas.drawRect(rect, strokePaint)
//    }
//}
////package com.tech.sid
////
////import android.content.Context
////import android.graphics.Canvas
////import android.graphics.Color
////import android.graphics.LinearGradient
////import android.graphics.Paint
////import android.graphics.Shader
////import android.util.AttributeSet
////import android.widget.LinearLayout
////
////class GradientView @JvmOverloads constructor(
////    context: Context,
////    attrs: AttributeSet? = null
////) : LinearLayout(context, attrs) {
////
////    private val paint = Paint()
////    private var gradientColors: IntArray = intArrayOf(Color.RED, Color.BLUE)
////
////    init {
////        attrs?.let {
////            val typedArray = context.obtainStyledAttributes(it, R.styleable.GradientViewSubscription)
////            val startColor = typedArray.getColor(R.styleable.GradientViewSubscription_startColor, Color.RED)
////            val endColor = typedArray.getColor(R.styleable.GradientViewSubscription_endColor, Color.BLUE)
////            gradientColors = intArrayOf(startColor, endColor)
////            typedArray.recycle()
////        }
////        setWillNotDraw(false) // Enable custom drawing
////    }
////
////    // Setter for startColor (for data binding)
////    fun setStartColor(startColor: Int) {
////        gradientColors = intArrayOf(startColor, gradientColors.getOrElse(1) { Color.BLUE })
////        invalidate()
////    }
////
////    // Setter for endColor (for data binding)
////    fun setEndColor(endColor: Int) {
////        gradientColors = intArrayOf(gradientColors.getOrElse(0) { Color.RED }, endColor)
////        invalidate()
////    }
////
////    // Setter for gradient colors (for programmatic use)
////    fun setGradientColors(colors: IntArray) {
////        gradientColors = colors
////        invalidate()
////    }
////
////    override fun onDraw(canvas: Canvas) {
////        super.onDraw(canvas)
////        val shader = LinearGradient(
////            0f, 0f, 0f, height.toFloat(),
////            gradientColors,
////            null,
////            Shader.TileMode.CLAMP
////        )
////        paint.shader = shader
////        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
////    }
////}