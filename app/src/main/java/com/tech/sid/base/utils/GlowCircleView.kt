package com.tech.sid.base.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.tech.sid.R

class GlowCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val blurRadius = 5f
    private var glowColor = Color.parseColor("#CAB8FF")
    private var fillColor = Color.parseColor("#CAB8FF")
    private var circleSizeDp = 0f // 0f means use screen-based sizing
    private var internalPaddingDp = 8f

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val solidPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        context.theme.obtainStyledAttributes(attrs, R.styleable.GlowCircleView, 0, 0).apply {
            try {
                glowColor = getColor(R.styleable.GlowCircleView_glowColor, glowColor)
                fillColor = getColor(R.styleable.GlowCircleView_fillColor, fillColor)
                circleSizeDp = getDimension(R.styleable.GlowCircleView_circleSize, 0f) / context.resources.displayMetrics.density
                internalPaddingDp = getDimension(R.styleable.GlowCircleView_internalPadding, 8f) / context.resources.displayMetrics.density
            } finally {
                recycle()
            }
        }

        glowPaint.color = glowColor
        glowPaint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
        solidPaint.color = fillColor
    }

    fun setGlowColor(color: Int) {
        glowColor = color
        glowPaint.color = color
        invalidate()
    }

    fun setFillColor(color: Int) {
        fillColor = color
        solidPaint.color = color
        invalidate()
    }

    fun setPaddingDp(paddingDp: Float) {
        internalPaddingDp = paddingDp
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f

        val maxRadius = (width.coerceAtMost(height) / 2f)
        val glowRadius = maxRadius - blurRadius
        val solidRadius = glowRadius - internalPaddingDp.dpToPx(context)

        canvas.drawCircle(centerX, centerY, glowRadius, glowPaint)
        canvas.drawCircle(centerX, centerY, solidRadius, solidPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
        val minScreenDp = screenWidthDp.coerceAtMost(screenHeightDp)

        val defaultSizeDp = minScreenDp * 0.2f

        val size = if (circleSizeDp > 0f) {
            circleSizeDp.dpToPx(context).toInt()
        } else {
            defaultSizeDp.dpToPx(context).toInt()
        }

        setMeasuredDimension(size, size)
    }
}

class GlowCircleView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val blurRadius = 5f
    private val strokeWidthDp = 1f
    private var glowColor = Color.GRAY
    private var fillColor = Color.WHITE
    private var strokeColor = Color.GREEN
    private var circleSizeDp = 0f
    private var internalPaddingDp = 8f

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val solidPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        context.theme.obtainStyledAttributes(attrs, R.styleable.GlowCircleView2, 0, 0).apply {
            try {
                glowColor = getColor(R.styleable.GlowCircleView2_glowColor2, glowColor)
                fillColor = getColor(R.styleable.GlowCircleView2_fillColor2, fillColor)
                strokeColor = getColor(R.styleable.GlowCircleView2_strokeColor2, strokeColor)
                circleSizeDp = getDimension(R.styleable.GlowCircleView2_circleSize2, 0f) / context.resources.displayMetrics.density
                internalPaddingDp = getDimension(R.styleable.GlowCircleView2_internalPadding2, 8f) / context.resources.displayMetrics.density
            } finally {
                recycle()
            }
        }

        glowPaint.color = glowColor
        glowPaint.style = Paint.Style.FILL
        glowPaint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

        solidPaint.color = fillColor
        solidPaint.style = Paint.Style.FILL

        strokePaint.color = strokeColor
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidthDp.dpToPx(context)
    }

    fun setPaddingDp(paddingDp: Float) {
        internalPaddingDp = paddingDp
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f

        val maxRadius = (width.coerceAtMost(height) / 2f)
        val glowRadius = maxRadius - blurRadius
        val solidRadius = glowRadius - internalPaddingDp.dpToPx(context)
        val strokeRadius = solidRadius - (strokePaint.strokeWidth / 2f)

        canvas.drawCircle(centerX, centerY, glowRadius, glowPaint)
        canvas.drawCircle(centerX, centerY, solidRadius, solidPaint)
        canvas.drawCircle(centerX, centerY, strokeRadius, strokePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
        val minScreenDp = screenWidthDp.coerceAtMost(screenHeightDp)

        val defaultSizeDp = minScreenDp * 0.2f

        val size = if (circleSizeDp > 0f) {
            circleSizeDp.dpToPx(context).toInt()
        } else {
            defaultSizeDp.dpToPx(context).toInt()
        }

        setMeasuredDimension(size, size)
    }
}
fun Int.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

fun Float.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

/*
package com.tech.sid.base.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.tech.sid.R

class GlowCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    View(context, attrs) {

    private val blurRadius = 5f
    private var glowColor = Color.parseColor("#CAB8FF")
    private var fillColor = Color.parseColor("#CAB8FF")
    private var circleSizeDp = 0f // 0f means use screen-based sizing

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val solidPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun setGlowColor(color: Int) {
        glowColor = color
        glowPaint.color = color
        invalidate()
    }

    fun setFillColor(color: Int) {
        fillColor = color
        solidPaint.color = color
        invalidate()
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        context.theme.obtainStyledAttributes(attrs, R.styleable.GlowCircleView, 0, 0).apply {
            try {
                glowColor = getColor(R.styleable.GlowCircleView_glowColor, glowColor)
                fillColor = getColor(R.styleable.GlowCircleView_fillColor, fillColor)
                circleSizeDp = getDimension(R.styleable.GlowCircleView_circleSize, 0f) / context.resources.displayMetrics.density
            } finally {
                recycle()
            }
        }

        glowPaint.color = glowColor
        glowPaint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
        solidPaint.color = fillColor
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f

        val maxRadius = (width.coerceAtMost(height) / 2f)
        val glowRadius = maxRadius - blurRadius
        val solidRadius = glowRadius - 8f

        canvas.drawCircle(centerX, centerY, glowRadius, glowPaint)
        canvas.drawCircle(centerX, centerY, solidRadius, solidPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val screenWidth = context.resources.displayMetrics.widthPixels
//        val screenHeight = context.resources.displayMetrics.heightPixels
//        val defaultSize = (screenWidth.coerceAtMost(screenHeight) * 0.2f).toInt() // 20% of smaller dimension
//
//        val size = if (circleSizeDp > 0f) {
//            // Use XML-defined size if provided
//            circleSizeDp.dpToPx(context).toInt()
//        } else {
//            // Use screen-based size, respecting parent constraints
//            val widthSize = resolveSize(defaultSize, widthMeasureSpec)
//            val heightSize = resolveSize(defaultSize, heightMeasureSpec)
//            widthSize.coerceAtMost(heightSize)
//        }
//
//        setMeasuredDimension(size, size)
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
        val minScreenDp = screenWidthDp.coerceAtMost(screenHeightDp)

        val defaultSizeDp = minScreenDp * 0.2f // 20% of smaller screen dimension in dp

        val size = if (circleSizeDp > 0f) {
            // Use XML-defined size if provided
            circleSizeDp.dpToPx(context).toInt()
        } else {
            // Convert default dp size to px
            defaultSizeDp.dpToPx(context).toInt()
        }

        setMeasuredDimension(size, size)
    }
}

class GlowCircleView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    View(context, attrs) {

    private val blurRadius = 5f
    private val strokeWidthDp = 1f
    private var glowColor = Color.GRAY
    private var fillColor = Color.WHITE
    private var strokeColor = Color.GREEN
    private var circleSizeDp = 0f // 0f means use screen-based sizing

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val solidPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        context.theme.obtainStyledAttributes(attrs, R.styleable.GlowCircleView2, 0, 0).apply {
            try {
                glowColor = getColor(R.styleable.GlowCircleView2_glowColor2, glowColor)
                fillColor = getColor(R.styleable.GlowCircleView2_fillColor2, fillColor)
                strokeColor = getColor(R.styleable.GlowCircleView2_strokeColor2, strokeColor)
                circleSizeDp = getDimension(R.styleable.GlowCircleView2_circleSize2, 0f) / context.resources.displayMetrics.density
            } finally {
                recycle()
            }
        }

        glowPaint.color = glowColor
        glowPaint.style = Paint.Style.FILL
        glowPaint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

        solidPaint.color = fillColor
        solidPaint.style = Paint.Style.FILL

        strokePaint.color = strokeColor
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidthDp.dpToPx(context)
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f

        val maxRadius = (width.coerceAtMost(height) / 2f)
        val glowRadius = maxRadius - blurRadius
        val solidRadius = glowRadius - 8f
        val strokeRadius = solidRadius - (strokePaint.strokeWidth / 2f)

        canvas.drawCircle(centerX, centerY, glowRadius, glowPaint)
        canvas.drawCircle(centerX, centerY, solidRadius, solidPaint)
        canvas.drawCircle(centerX, centerY, strokeRadius, strokePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val screenWidth = context.resources.displayMetrics.widthPixels
//        val screenHeight = context.resources.displayMetrics.heightPixels
//        val defaultSize = (screenWidth.coerceAtMost(screenHeight) * 0.2f).toInt() // 20% of smaller dimension
//
//        val size = if (circleSizeDp > 0f) {
//            // Use XML-defined size if provided
//            circleSizeDp.dpToPx(context).toInt()
//        } else {
//            // Use screen-based size, respecting parent constraints
//            val widthSize = resolveSize(defaultSize, widthMeasureSpec)
//            val heightSize = resolveSize(defaultSize, heightMeasureSpec)
//            widthSize.coerceAtMost(heightSize)
//        }
//
//        setMeasuredDimension(size, size)
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
        val minScreenDp = screenWidthDp.coerceAtMost(screenHeightDp)

        val defaultSizeDp = minScreenDp * 0.2f // 20% of smaller screen dimension in dp

        val size = if (circleSizeDp > 0f) {
            // Use XML-defined size if provided
            circleSizeDp.dpToPx(context).toInt()
        } else {
            // Convert default dp size to px
            defaultSizeDp.dpToPx(context).toInt()
        }

        setMeasuredDimension(size, size)
    }
}

fun Int.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

fun Float.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}



*/
