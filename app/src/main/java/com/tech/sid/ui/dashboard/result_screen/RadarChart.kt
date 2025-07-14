package com.tech.sid.ui.dashboard.result_screen
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.tech.sid.R
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class RadarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var labels: List<String> = listOf("Openness", "Agreeableness", "Gratitude", "Conscientiousness", "Neuroticism", "Cognitive Style")
    private var values: List<Float> = listOf(10f, 10f, 10f, 10f, 10f, 10f)
    private var maxValue: Float = 10f
    private val labelPadding = 5f // Minimal padding for labels
    private val webPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.graph_boarder_width_line)
        strokeWidth = 2f
        isAntiAlias = true
    }
    private val dataPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK // Data line color
        strokeWidth = 3f // No stroke for data
        isAntiAlias = true
    }
    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        color =  ContextCompat.getColor(context, R.color.text_color_splash)
        textSize = 8f * resources.displayMetrics.scaledDensity // Reduced text size for smaller height
        isAntiAlias = true
        typeface = ResourcesCompat.getFont(context, R.font.dm_sans_medium) // Fallback to Roboto
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setChartData(labels: List<String>, values: List<Float>, label: String = "Usage") {
        if (labels.size != values.size) {
            throw IllegalArgumentException("Labels and values must have the same size")
        }
        this.labels = labels
        this.values = values
        this.maxValue = values.maxOrNull() ?: 5f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (canvas == null) return

        val centerX = width / 2f
        val centerY = height / 2f
        // Calculate max text width and height for label space
        val maxTextWidth = labels.maxOfOrNull { textPaint.measureText(it) } ?: 0f
        val textHeight = textPaint.descent() - textPaint.ascent()
        // Use min of width and height, accounting for label space
        val radius = min(width / 2f - maxTextWidth - labelPadding, height / 2f - textHeight - labelPadding)
        val circleCount = 5 // Number of concentric circles

        // Draw concentric circles
        for (i in 1..circleCount) {
            val r = (i / circleCount.toFloat()) * radius
            canvas.drawCircle(centerX, centerY, r, webPaint)
        }

        // Draw radial lines and labels
        val sliceAngle = 360f / labels.size
        for (i in labels.indices) {
            val angle = Math.toRadians((i * sliceAngle).toDouble()).toFloat()
            val px = centerX + radius * cos(angle)
            val py = centerY + radius * sin(angle)
            canvas.drawLine(centerX, centerY, px, py, webPaint)

            // Draw labels with minimal offset
            val labelRadius = radius + labelPadding
            val textX = centerX + labelRadius * cos(angle)
            val textY = centerY + labelRadius * sin(angle)
            val label = labels[i]
            val textWidth = textPaint.measureText(label)
            val adjustedX = when {
                textX < centerX -> textX - textWidth - 3f // Shift left labels
                textX > centerX -> textX + 3f // Shift right labels
                else -> textX - textWidth / 2 // Center horizontally
            }
            val adjustedY = when {
                textY < centerY -> textY // No vertical shift up
                textY > centerY -> textY // No vertical shift down
                else -> textY // Center vertically
            }
            canvas.drawText(label, adjustedX, adjustedY, textPaint)
        }

        // Draw dataset
        val path = Path()
        for (i in values.indices) {
            val value = values[i]
            val angle = Math.toRadians((i * sliceAngle).toDouble()).toFloat()
            val r = (value / maxValue) * radius
            val px = centerX + r * cos(angle)
            val py = centerY + r * sin(angle)
            if (i == 0) {
                path.moveTo(px, py)
            } else {
                path.lineTo(px, py)
            }
        }
        path.close()

        // Apply gradient
        val bounds = RectF()
        path.computeBounds(bounds, true)
        fillPaint.shader = LinearGradient(
            bounds.centerX(), bounds.top, bounds.centerX(), bounds.bottom,
            Color.parseColor("#B3D8CCFF"), // Start color
            Color.parseColor("#B3A8FFFF"), // End color
            Shader.TileMode.CLAMP
        )

        // Draw filled path
        canvas.drawPath(path, fillPaint)
        canvas.drawPath(path, dataPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Calculate extra space needed for labels
        val maxTextWidth = labels.maxOfOrNull { textPaint.measureText(it) } ?: 0f
        val textHeight = textPaint.descent() - textPaint.ascent()
        val extraSpace = maxTextWidth + labelPadding // Horizontal space for labels
        val minSize = 100f // Reduced minimum size to allow smaller height

        // Resolve width and height independently
        val desiredSize = (minSize + extraSpace).toInt()
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)

        // Use the specified height if provided, otherwise use desiredSize
        val finalHeight = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            height
        }

        // Ensure width is at least as large as height to maintain chart proportions
        val finalWidth = max(width, finalHeight)
        setMeasuredDimension(finalWidth, finalHeight)
    }
}
