package com.example.fetchapp

import android.graphics.Rect
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchapp.view.ItemAdapter


class StickyHeaderClickListener(
    private val recyclerView: RecyclerView,
    private val adapter: ItemAdapter,
    private val headerClickListener: (Int) -> Unit
) : RecyclerView.SimpleOnItemTouchListener() {

    private val gestureDetector = GestureDetector(recyclerView.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val child = recyclerView.findChildViewUnder(e.x, e.y)
            if (child != null) {
                val position = recyclerView.getChildAdapterPosition(child)
                if (adapter.isHeader(position)) {
                    headerClickListener(adapter.getHeaderPositionForItem(position))
                    return true
                }
            }
            return false
        }
    })

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val stickyHeader = getStickyHeader(rv)
        if (stickyHeader != null) {
            val rect = Rect()
            stickyHeader.getGlobalVisibleRect(rect)
            if (rect.contains(e.rawX.toInt(), e.rawY.toInt())) {
                if (e.action == MotionEvent.ACTION_UP) {
                    // Handle sticky header click
                    val position = adapter.getHeaderPositionForItem(rv.getChildAdapterPosition(rv.getChildAt(0)))
                    headerClickListener(position)
                    return true
                }
            }
        }
        return gestureDetector.onTouchEvent(e)
    }

    private fun getStickyHeader(rv: RecyclerView): View? {
        return rv.getChildAt(0)  // The sticky header is always at position 0
    }
}