package com.example.fetchapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchapp.R
import com.example.fetchapp.model.Item

class ItemAdapter(private var groupedItems: Map<Int, List<Item>> = emptyMap()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    // Flat list the RecyclerView shows: a listId (Int) is a header row, an Item is an item row.
    private val data: MutableList<Any> = mutableListOf()
    private val expandedStateMap = mutableMapOf<Int, Boolean>()

    init {
        updateData(groupedItems)
    }

    // Rebuilds the flat list. Sections that were expanded stay expanded.
    fun updateData(newGroupedItems: Map<Int, List<Item>>) {
        groupedItems = newGroupedItems
        data.clear()
        expandedStateMap.keys.retainAll(newGroupedItems.keys)
        groupedItems.forEach { (listId, items) ->
            data.add(listId)
            if (expandedStateMap[listId] == true) {
                data.addAll(items)
            } else {
                expandedStateMap[listId] = false
            }
        }
        notifyDataSetChanged()
    }

    fun getExpandedListIds(): List<Int> = expandedStateMap.filterValues { it }.keys.toList()

    // Call before the data arrives to re-open sections (for example after a rotation).
    fun restoreExpandedListIds(listIds: Collection<Int>) {
        listIds.forEach { expandedStateMap[it] = true }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] is Int) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_header_layout, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            (holder as HeaderViewHolder).bind(data[position] as Int)
        } else {
            (holder as ItemViewHolder).bind(data[position] as Item)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTextView: TextView = itemView.findViewById(R.id.listHeaderTextView)

        fun bind(listId: Int) {
            headerTextView.text = "List ID: $listId"
            itemView.setOnClickListener {
                val expand = !(expandedStateMap[listId] ?: false)
                expandedStateMap[listId] = expand
                toggleItemsForListId(listId, expand)
            }
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemTextView: TextView = itemView.findViewById(R.id.itemTextView)

        fun bind(item: Item) {
            itemTextView.text = "Name: ${item.name}"
        }
    }

    private fun toggleItemsForListId(listId: Int, expand: Boolean) {
        val index = data.indexOf(listId)
        if (index == -1) return

        val items = groupedItems[listId] ?: emptyList()
        if (expand) {
            data.addAll(index + 1, items)
            notifyItemRangeInserted(index + 1, items.size)
        } else {
            data.subList(index + 1, index + 1 + items.size).clear()
            notifyItemRangeRemoved(index + 1, items.size)
        }
    }

    // Safe for out of range positions (for example RecyclerView.NO_POSITION during animations).
    fun isHeader(position: Int): Boolean {
        return position in data.indices && data[position] is Int
    }

    fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = 0
        for (i in itemPosition downTo 0) {
            if (isHeader(i)) {
                headerPosition = i
                break
            }
        }
        return headerPosition
    }

    // The listId shown by the header row at this position.
    fun getHeaderListId(headerPosition: Int): Int = data[headerPosition] as Int

    fun getHeaderViewForItem(headerPosition: Int, parent: RecyclerView): View {
        val viewHolder = onCreateViewHolder(parent, VIEW_TYPE_HEADER)
        onBindViewHolder(viewHolder, headerPosition)
        return viewHolder.itemView
    }
}
