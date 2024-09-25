package com.example.fetchapp.view

import android.util.Log
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
    private val data: MutableList<Any> = mutableListOf()
    private val expandedStateMap = mutableMapOf<Int, Boolean>()  // Track expanded/collapsed states

    init {
        updateData(groupedItems)
    }

    // Flatten the grouped items and update the adapter
    fun updateData(newGroupedItems: Map<Int, List<Item>>) {
        Log.d("ItemAdapter", "Updating data: $newGroupedItems")
        groupedItems = newGroupedItems  // Store the groupedItems map inside the adapter
        data.clear()
        expandedStateMap.clear()
        groupedItems.forEach { (listId, _) ->
            data.add(listId) // Add section header (listId)
            expandedStateMap[listId] = false  // Initialize all listIds as collapsed
        }
        notifyDataSetChanged()  // Ensure the RecyclerView is updated with the new data
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
            val listId = data[position] as Int
            Log.d("ItemAdapter", "Binding header for List ID: $listId at position $position")
            (holder as HeaderViewHolder).bind(listId)
        } else {
            val item = data[position] as Item
            Log.d("ItemAdapter", "Binding item: ${item.name} at position $position")
            (holder as ItemViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = data.size

    // ViewHolder for Header (List ID)
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTextView: TextView = itemView.findViewById(R.id.listHeaderTextView)

        fun bind(listId: Int) {
            headerTextView.text = "List ID: $listId"
            val isExpanded = expandedStateMap[listId] ?: false
            Log.d("ItemAdapter", "Binding header for List ID: $listId, isExpanded: $isExpanded")
            // Set click listener to toggle expand/collapse
            itemView.setOnClickListener {
                Log.d("ItemAdapter", "Header clicked for List ID: $listId")
                val isExpanded = expandedStateMap[listId] ?: false
                Log.d("ItemAdapter", "Toggling List ID: $listId, isExpanded: $isExpanded")
                expandedStateMap[listId] = !isExpanded  // Toggle state
                toggleItemsForListId(listId, !isExpanded)  // Pass the new expanded state
            }
        }
    }

    // ViewHolder for Item (Item Name)
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemTextView: TextView = itemView.findViewById(R.id.itemTextView)

        fun bind(item: Item) {
            itemTextView.text = "Name: ${item.name}"

        }
    }

    // Show or hide items for the given listId
    private fun toggleItemsForListId(listId: Int, expand: Boolean) {
        val index = data.indexOf(listId)
        Log.d("ItemAdapter", "toggleItemsForListId called for List ID: $listId at index: $index, expand: $expand")

        if (index != -1) {
            if (expand) {
                // Expand: Insert the items under this listId
                val items = groupedItems[listId] ?: emptyList()
                Log.d("ItemAdapter", "Expanding List ID: $listId, inserting ${items.size} items")
                data.addAll(index + 1, items)
                notifyItemRangeInserted(index + 1, items.size)
            } else {
                // Collapse: Remove the items under this listId
                val itemsToRemove = groupedItems[listId] ?: emptyList()
                Log.d("ItemAdapter", "Collapsing List ID: $listId, removing ${itemsToRemove.size} items")
                data.subList(index + 1, index + 1 + itemsToRemove.size).clear()
                notifyItemRangeRemoved(index + 1, itemsToRemove.size)
            }
        } else {
            Log.e("ItemAdapter", "List ID: $listId not found in data")
        }
    }

    // Method to determine if an item is a header
    fun isHeader(position: Int): Boolean {
        return data[position] is Int
    }

    // Method to get the header position for a given item
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

    // Method to retrieve the header view
    fun getHeaderViewForItem(headerPosition: Int, parent: RecyclerView): View {
        val viewHolder = onCreateViewHolder(parent, VIEW_TYPE_HEADER)
        onBindViewHolder(viewHolder, headerPosition)
        return viewHolder.itemView
    }
}







