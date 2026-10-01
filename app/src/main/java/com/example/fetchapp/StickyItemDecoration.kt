package com.example.fetchapp

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchapp.view.ItemAdapter

class StickyHeaderItemDecoration(private val adapter: ItemAdapter) : RecyclerView.ItemDecoration() {

    // Header views keyed by listId, not by adapter position. Positions shift whenever a
    // section expands or collapses, which would pin the wrong header.
    private val headerCache = mutableMapOf<Int, View>()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) return

        val currentHeaderPos = adapter.getHeaderPositionForItem(topChildPosition)
        if (!adapter.isHeader(currentHeaderPos)) return

        val header = getHeaderViewForPosition(parent, currentHeaderPos)
        fixLayoutSize(parent, header)
        header.translationZ = 10f

        val childInContact = getChildInContact(parent, header.bottom)
        if (childInContact != null && adapter.isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, header, childInContact)
        } else {
            drawHeader(c, header)
        }
    }

    private fun getHeaderViewForPosition(parent: RecyclerView, position: Int): View {
        return headerCache.getOrPut(adapter.getHeaderListId(position)) {
            val header = adapter.getHeaderViewForItem(position, parent)
            fixLayoutSize(parent, header)
            header
        }
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
        if (view.measuredWidth == 0 || view.measuredHeight == 0) {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
            view.measure(widthSpec, heightSpec)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        }
    }
}
