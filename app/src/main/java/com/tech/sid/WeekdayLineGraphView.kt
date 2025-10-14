package com.tech.sid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat

class WeekdayLineGraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val weekdays = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")

    private var values: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0)
    private var maxValue: Int = 10
    private var selectedIndex: Int? = 0 // Default to Monday (index 2)

    private val linePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 1f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#433F70")
        textSize = 25f
        textAlign = Paint.Align.CENTER
    }

    private val circleFillPaint = Paint().apply {
        color = Color.WHITE // White fill color
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val circleStrokePaint = Paint().apply {
        color = Color.parseColor("#D8CCFF") // Stroke color
        style = Paint.Style.STROKE
        strokeWidth = 1f * resources.displayMetrics.density // 1dp stroke
        isAntiAlias = true
    }

    init {
        val typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
        textPaint.typeface = typeface
    }

    // Function to set data and trigger redraw
    fun setGraphData(newValues: List<Int>, newMaxValue: Int) {
        values = newValues
        maxValue = newMaxValue
     //   selectedIndex = 2 // Default to Monday (index 2) when data changes
        invalidate() // Triggers onDraw()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Handle tap to select the nearest weekday
/*            val leftRightPadding = 60f
            val graphWidth = width - 2 * leftRightPadding
            val xInterval = graphWidth / (weekdays.size - 1)

            val tapX = event.x
            // Find the closest weekday index based on tap x-coordinate
            val closestIndex = ((tapX - leftRightPadding) / xInterval).toInt().coerceIn(0, weekdays.size - 1)
            selectedIndex = closestIndex
            invalidate() // Redraw to show the circle*/
            return false
        }
        return super.onTouchEvent(event)
    }

    fun setDot(data :Int){
        selectedIndex = data
        invalidate() // Redraw to show the circle
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (values.size != weekdays.size) return

        val leftRightPadding = 60f
        val topPadding = 15f
        val bottomPadding = 80f

        val graphHeight = height - topPadding - bottomPadding
        val graphWidth = width - 2 * leftRightPadding

        val xInterval = graphWidth / (weekdays.size - 1)
        val xAxisY = height - bottomPadding

        val dashSegmentCount = 30
        val totalWidth = width - 2 * leftRightPadding
        val dashSegmentWidth = totalWidth / dashSegmentCount.toFloat()

        val horizontalLinePaint = Paint().apply {
            color = Color.parseColor("#D9D9D9")
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
            pathEffect = DashPathEffect(floatArrayOf(dashSegmentWidth / 2, dashSegmentWidth / 2), 0f)
        }
        val lineCount = 6
        val initialGap = 40f
        val availableHeight = graphHeight - initialGap
        val spacing = availableHeight / (lineCount - 1)
        for (i in 0 until lineCount) {
            val y = xAxisY - initialGap - i * spacing
            canvas.drawLine(leftRightPadding, y, width - leftRightPadding, y, horizontalLinePaint)
        }

        // Apply gradient to linePaint dynamically
        val gradient = LinearGradient(
            leftRightPadding, 0f, width - leftRightPadding, 0f,
            intArrayOf(Color.parseColor("#9773FF"), Color.parseColor("#00ACAC")),
            null,
            Shader.TileMode.CLAMP
        )
        linePaint.shader = gradient

        // Draw weekday labels
        weekdays.forEachIndexed { index, day ->
            val x = leftRightPadding + index * xInterval
            canvas.drawText(day, x, xAxisY + 40f, textPaint)
        }

        // Draw gradient line
        for (i in 0 until values.size - 1) {
            val x1 = leftRightPadding + i * xInterval
            val x2 = leftRightPadding + (i + 1) * xInterval

            val y1 = xAxisY - (values[i] / maxValue.toFloat()) * graphHeight
            val y2 = xAxisY - (values[i + 1] / maxValue.toFloat()) * graphHeight

            canvas.drawLine(x1, y1, x2, y2, linePaint)
        }

        // Draw circle at the selected weekday point
        selectedIndex?.let { index ->
            val x = leftRightPadding + index * xInterval
            val y = xAxisY - (values[index] / maxValue.toFloat()) * graphHeight
            val radius = 5f * resources.displayMetrics.density // 5dp radius
            canvas.drawCircle(x, y, radius, circleFillPaint) // Draw white fill
            canvas.drawCircle(x, y, radius, circleStrokePaint) // Draw stroke
        }
    }
}

