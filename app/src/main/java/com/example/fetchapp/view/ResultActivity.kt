package com.example.fetchapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchapp.R
import com.example.fetchapp.StickyHeaderItemDecoration
import com.example.fetchapp.model.ItemRepository
import com.example.fetchapp.model.ItemViewModelFactory
import com.example.fetchapp.viewmodel.ItemViewModel

class ResultActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var viewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(mapOf())
        recyclerView.adapter = adapter

        // Keep the current section header pinned to the top while scrolling.
        recyclerView.addItemDecoration(StickyHeaderItemDecoration(adapter))

        // Re-open the sections that were open before a rotation, once the data arrives.
        savedInstanceState?.getIntegerArrayList(KEY_EXPANDED)?.let {
            adapter.restoreExpandedListIds(it)
        }

        val factory = ItemViewModelFactory(ItemRepository())
        viewModel = ViewModelProvider(this, factory)[ItemViewModel::class.java]

        viewModel.items.observe(this) { groupedItems ->
            if (groupedItems != null) {
                adapter.updateData(groupedItems)
            }
        }

        viewModel.error.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                viewModel.onErrorShown()
            }
        }

        viewModel.fetchItems()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList(KEY_EXPANDED, ArrayList(adapter.getExpandedListIds()))
    }

    private companion object {
        const val KEY_EXPANDED = "expanded_list_ids"
    }
}
