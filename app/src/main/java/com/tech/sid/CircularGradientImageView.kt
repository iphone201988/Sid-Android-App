package com.tech.sid

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

class CircularGradientImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private val strokeWidth = 2 * resources.displayMetrics.density // 2dp in pixels
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var bitmapShader: BitmapShader? = null

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val radius = min(viewWidth, viewHeight) / 2f
        val centerX = viewWidth / 2f
        val centerY = viewHeight / 2f

        // Convert drawable to bitmap
        val bmp = getBitmapFromDrawable(drawable) ?: return

        // Create CENTER_CROP style shader matrix
        bitmapShader = BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val shaderMatrix = Matrix()

        val scale: Float
        val dx: Float
        val dy: Float

        if (bmp.width * viewHeight > viewWidth * bmp.height) {
            scale = viewHeight / bmp.height.toFloat()
            dx = (viewWidth - bmp.width * scale) * 0.5f
            dy = 0f
        } else {
            scale = viewWidth / bmp.width.toFloat()
            dx = 0f
            dy = (viewHeight - bmp.height * scale) * 0.5f
        }

        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(dx, dy)
        bitmapShader?.setLocalMatrix(shaderMatrix)

        imagePaint.shader = bitmapShader

        // Draw circular image
        canvas.drawCircle(centerX, centerY, radius - strokeWidth, imagePaint)

        // Create gradient stroke
        val gradient = LinearGradient(
            0f, 0f, viewWidth, viewHeight,
            Color.parseColor("#9773FF"),
            Color.parseColor("#00ACAC"),
            Shader.TileMode.CLAMP
        )

        strokePaint.shader = gradient
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth

        // Draw circular stroke
        canvas.drawCircle(centerX, centerY, radius - strokeWidth / 2, strokePaint)
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            else -> {
                val width = drawable.intrinsicWidth
                val height = drawable.intrinsicHeight
                if (width <= 0 || height <= 0) return null

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
        }
    }
}
