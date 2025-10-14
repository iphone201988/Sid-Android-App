package com.tech.sid.base.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NonScrollableRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private var initialX = 0f
    private var initialY = 0f
    private val touchSlop = 10 // Adjust this threshold as needed

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        e ?: return false

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                // Store initial touch coordinates
                initialX = e.x
                initialY = e.y
                // Allow children (inner RecyclerView) to handle touch events
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                // Calculate touch movement
                val deltaX = Math.abs(e.x - initialX)
                val deltaY = Math.abs(e.y - initialY)

                // If significant movement, let inner RecyclerView handle horizontal swipes
                if (deltaX > deltaY && deltaX > touchSlop) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    return false // Pass horizontal swipes to inner RecyclerView
                }
                // Allow vertical movement to be handled by parent if needed
                if (deltaY > deltaX && deltaY > touchSlop) {
                    parent.requestDisallowInterceptTouchEvent(false)
                    return false // Allow parent to handle vertical scrolling
                }
            }
        }
        return false // By default, let children handle touch events
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        // Don't consume touch events in the outer RecyclerView
        return false
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        // Support PagerSnapHelper if used
        return true
    }

    override fun canScrollVertically(direction: Int): Boolean {
        // Prevent vertical scrolling
        return false
    }
}
//package com.tech.sid.base.utils
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.MotionEvent
//import androidx.recyclerview.widget.RecyclerView
//
//class NonScrollableRecyclerView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null
//) : RecyclerView(context, attrs) {
//
//    private var initialX = 0f
//    private var initialY = 0f
//    private val touchSlop = 10 // Adjust this threshold as needed
//
//    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
//        e ?: return false
//
//        when (e.action) {
//            MotionEvent.ACTION_DOWN -> {
//                // Store initial touch coordinates
//                initialX = e.x
//                initialY = e.y
//                // Allow children to receive the touch event
//                parent.requestDisallowInterceptTouchEvent(true)
//            }
//            MotionEvent.ACTION_MOVE -> {
//                // Calculate touch movement
//                val deltaX = Math.abs(e.x - initialX)
//                val deltaY = Math.abs(e.y - initialY)
//
//                // If vertical movement is significant, let the inner RecyclerView handle it
//                if (deltaY > deltaX && deltaY > touchSlop) {
//                    parent.requestDisallowInterceptTouchEvent(true)
//                    return false // Pass touch to inner RecyclerView for vertical scrolling
//                }
//                // If horizontal movement is significant, intercept to block outer scrolling
//                if (deltaX > deltaY && deltaX > touchSlop) {
//                    return true // Block horizontal scrolling
//                }
//            }
//        }
//        return false // By default, let children handle touch events
//    }
//
//    override fun onTouchEvent(e: MotionEvent?): Boolean {
//        // Don't consume touch events in the outer RecyclerView
//        return false
//    }
//
//    override fun canScrollHorizontally(direction: Int): Boolean {
//        // Allow programmatic scrolling for PagerSnapHelper
//        return true
//    }
//}
//
//
//
