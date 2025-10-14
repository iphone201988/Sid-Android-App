package com.tech.sid.base.utils

import com.tech.sid.R

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout



class RoundedConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StyledConstraintLayout)

        val cornerRadius = typedArray.getDimension(R.styleable.StyledConstraintLayout_cornerRadius, 0f)
        val strokeWidth = typedArray.getDimensionPixelSize(R.styleable.StyledConstraintLayout_strokeWidth, 0)
        val strokeColor = typedArray.getColor(R.styleable.StyledConstraintLayout_strokeColor, Color.TRANSPARENT)
        val solidColor = typedArray.getColor(R.styleable.StyledConstraintLayout_solidColor, Color.WHITE)

        typedArray.recycle()

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(solidColor)
            setStroke(strokeWidth, strokeColor)
            this.cornerRadius = cornerRadius
        }

        background = drawable
    }
}