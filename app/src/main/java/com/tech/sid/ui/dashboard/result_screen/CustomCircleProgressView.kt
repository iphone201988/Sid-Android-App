package com.tech.sid.ui.dashboard.result_screen


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.tech.sid.R
import kotlin.math.atan2
import kotlin.math.min

class CustomCircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var value: Float = 10f
    private var maxValue: Float = 100f
    private var barColor: Int = Color.parseColor("#B5EAEA")
    private var rimColor: Int = Color.parseColor("#433F70")
    private var textColor: Int = Color.parseColor("#433F70")
    private var unitColor: Int = Color.parseColor("#433F70")
    private var barWidth: Float = 35f * resources.displayMetrics.density
    private var rimWidth: Float = 35f * resources.displayMetrics.density
    private var unit: String = "%"
    private var showUnit: Boolean = true
    private var unitPosition: String = "right_top"
    private var textScale: Float = 1f
    private var unitScale: Float = 1f
    private var autoTextSize: Boolean = true
    private var seekMode: Boolean = false // Set default to false (non-interactive)
    private var customText: String? = ""
    private var innerContourSize: Float = 0f
    private var outerContourSize: Float = 0f

    private val barPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val rimPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = ResourcesCompat.getFont(context, R.font.dm_sans_medium)
    }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomCircleProgressView, 0, 0)
            value = typedArray.getFloat(R.styleable.CustomCircleProgressView_cpv_value, 10f)
            maxValue = typedArray.getFloat(R.styleable.CustomCircleProgressView_cpv_maxValue, 100f)
            barColor = typedArray.getColor(R.styleable.CustomCircleProgressView_cpv_barColor, barColor)
            rimColor = typedArray.getColor(R.styleable.CustomCircleProgressView_cpv_rimColor, rimColor)
            textColor = typedArray.getColor(R.styleable.CustomCircleProgressView_cpv_textColor, textColor)
            unitColor = typedArray.getColor(R.styleable.CustomCircleProgressView_cpv_unitColor, unitColor)
            barWidth = typedArray.getDimension(R.styleable.CustomCircleProgressView_cpv_barWidth, barWidth)
            rimWidth = typedArray.getDimension(R.styleable.CustomCircleProgressView_cpv_rimWidth, rimWidth)
            unit = typedArray.getString(R.styleable.CustomCircleProgressView_cpv_unit) ?: unit
            showUnit = typedArray.getBoolean(R.styleable.CustomCircleProgressView_cpv_showUnit, true)
            unitPosition = typedArray.getString(R.styleable.CustomCircleProgressView_cpv_unitPosition) ?: unitPosition
            textScale = typedArray.getFloat(R.styleable.CustomCircleProgressView_cpv_textScale, 1f)
            unitScale = typedArray.getFloat(R.styleable.CustomCircleProgressView_cpv_unitScale, 1f)
            autoTextSize = typedArray.getBoolean(R.styleable.CustomCircleProgressView_cpv_autoTextSize, true)
            seekMode = typedArray.getBoolean(R.styleable.CustomCircleProgressView_cpv_seekMode, false)
            customText = typedArray.getString(R.styleable.CustomCircleProgressView_cpv_text) ?: customText
            innerContourSize = typedArray.getDimension(R.styleable.CustomCircleProgressView_cpv_innerContourSize, 0f)
            outerContourSize = typedArray.getDimension(R.styleable.CustomCircleProgressView_cpv_outerContourSize, 0f)
            typedArray.recycle()
        }
        setBackgroundColor(Color.TRANSPARENT)
    }
    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(width, height) / 2f - maxOf(barWidth, rimWidth) / 2f - maxOf(innerContourSize, outerContourSize)

        // Draw rim
        rimPaint.color = rimColor
        rimPaint.strokeWidth = rimWidth
        canvas.drawCircle(centerX, centerY, radius, rimPaint)

        // Draw progress arc
        barPaint.color = barColor
        barPaint.strokeWidth = barWidth
        val sweepAngle = (value / maxValue) * 360f
        canvas.drawArc(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius,
            -90f, sweepAngle, false, barPaint
        )

        // Draw multi-line text (support for \n)
        val displayText = customText ?: "${value.toInt()}${if (showUnit && unit.isNotEmpty()) unit else ""}"
        val lines = displayText.split("\n")

        // Dynamically calculate text size based on number of lines
        val baseTextSize = (radius * 0.3f * textScale).coerceAtMost(18f * resources.displayMetrics.scaledDensity)
        textPaint.color = textColor
        textPaint.textSize = baseTextSize

        val totalHeight = lines.size * (textPaint.fontSpacing)
        var startY = centerY - totalHeight / 2 + textPaint.fontSpacing

        for (line in lines) {
            val textWidth = textPaint.measureText(line)
            val textX = centerX - textWidth / 2
            canvas.drawText(line, textX, startY, textPaint)
            startY += textPaint.fontSpacing
        }
    }

//
//    override fun onDraw(canvas: Canvas) {
//        val centerX = width / 2f
//        val centerY = height / 2f
//        val radius = min(width, height) / 2f - maxOf(barWidth, rimWidth) / 2f - maxOf(innerContourSize, outerContourSize)
//
//        // Draw rim
//        rimPaint.color = rimColor
//        rimPaint.strokeWidth = rimWidth
//        canvas.drawCircle(centerX, centerY, radius, rimPaint)
//
//        // Draw progress arc
//        barPaint.color = barColor
//        barPaint.strokeWidth = barWidth
//        val sweepAngle = (value / maxValue) * 360f
//        canvas.drawArc(
//            centerX - radius, centerY - radius,
//            centerX + radius, centerY + radius,
//            -90f, sweepAngle, false, barPaint
//        )
//
//        // Prepare text
//        val displayText = customText ?: value.toInt().toString()
//        val textToShow = if (showUnit && unit.isNotEmpty()) "$displayText$unit" else displayText
//        textPaint.color = if (showUnit && unitPosition == "right_top") unitColor else textColor
//
//        // Auto-scale text
//        var textSize = radius * 0.5f * textScale
//        textPaint.textSize = textSize
//        while (textPaint.measureText(textToShow) > radius * 1.5f && textSize > 10f) {
//            textSize -= 2f
//            textPaint.textSize = textSize
//        }
//
//        // Center text
//        val textWidth = textPaint.measureText(textToShow)
//        val textX = centerX - textWidth / 2
//        val textBounds = Rect()
//        textPaint.getTextBounds(textToShow, 0, textToShow.length, textBounds)
//        val textY = centerY + textBounds.height() / 2f
//
//        canvas.drawText(textToShow, textX, textY, textPaint)
//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false // Fully disable touch interaction
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minSize = 100f + maxOf(barWidth, rimWidth)
        val desiredSize = minSize.toInt()
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    // Public control methods
    fun setValue(newValue: Float) {
        value = newValue.coerceIn(0f, maxValue)
        invalidate()
    }

    fun setValueAnimated(newValue: Float, duration: Long) {
        val animator = ValueAnimator.ofFloat(value, newValue.coerceIn(0f, maxValue))
        animator.duration = duration
        animator.addUpdateListener { animation ->
            value = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    fun setMaxValue(newMaxValue: Float) {
        maxValue = newMaxValue.coerceAtLeast(1f)
        value = value.coerceIn(0f, maxValue)
        invalidate()
    }

    fun setBarColor(color: Int) {
        barColor = color
        invalidate()
    }

    fun setRimColor(color: Int) {
        rimColor = color
        invalidate()
    }

    fun setTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    fun setUnit(newUnit: String) {
        unit = newUnit
        invalidate()
    }

    fun setShowUnit(show: Boolean) {
        showUnit = show
        invalidate()
    }
    fun setTextCustom(show: String) {
        customText = show
        invalidate()
    }
}
