package com.example.fetchapp

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchapp.view.ItemAdapter

class StickyHeaderItemDecoration(private val adapter: ItemAdapter) : RecyclerView.ItemDecoration() {

    private val headerCache = mutableMapOf<Int, View>()  // Cache to store header views

//    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        super.onDrawOver(c, parent, state)
//
//        val topChild = parent.getChildAt(0) ?: return
//        val topChildPosition = parent.getChildAdapterPosition(topChild)
//        if (topChildPosition == RecyclerView.NO_POSITION) return
//
//        val currentHeaderPosition = adapter.getHeaderPositionForItem(topChildPosition)
//        val header = getHeaderView(parent, currentHeaderPosition)
//        fixLayoutSize(parent, header)
//
//        val contactPoint = header.bottom
//        val childInContact = getChildInContact(parent, contactPoint) ?: return
//
//        if (adapter.isHeader(parent.getChildAdapterPosition(childInContact))) {
//            moveHeader(c, header, childInContact)
//        } else {
//            drawHeader(c, header)
//        }
//    }

    //claude
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) return

        val currentHeaderPos = adapter.getHeaderPositionForItem(topChildPosition)
        val header = getHeaderViewForPosition(parent, currentHeaderPos)
        fixLayoutSize(parent, header)
        //
        // Always set translationZ to bring the header to the front
        header.translationZ = 10f
        val contactPoint = header.bottom
        val childInContact = getChildInContact(parent, contactPoint)
        if (childInContact != null && adapter.isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, header, childInContact)
        } else {
            drawHeader(c, header)
        }
    }

    //claude
    private fun getHeaderViewForPosition(parent: RecyclerView, position: Int): View {
        return headerCache.getOrPut(position) {
            val header = adapter.getHeaderViewForItem(position, parent)
            fixLayoutSize(parent, header)
            header
        }
    }

    private fun getHeaderView(parent: RecyclerView, headerPosition: Int): View {
        if (!headerCache.containsKey(headerPosition)) {
            val header = adapter.getHeaderViewForItem(headerPosition, parent)
            fixLayoutSize(parent, header)
            headerCache[headerPosition] = header
        }
        return headerCache[headerPosition]!!
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.bottom > contactPoint && child.top <= contactPoint) {
                return child
            }
        }
        return null
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(0f, (nextHeader.top - currentHeader.height).toFloat())
        currentHeader.draw(c)
        c.restore()
    }

    private fun fixLayoutSize(parent: RecyclerView, view: View) {
        // Check if the view is already measured, no need to remeasure
        if (view.measuredWidth == 0 || view.measuredHeight == 0) {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
            view.measure(widthSpec, heightSpec)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        }
    }

}
