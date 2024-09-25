package com.example.fetchapp.view

import android.os.Bundle
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

        // Initialize RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Initialize the adapter with an empty map and set it to the RecyclerView
        adapter = ItemAdapter(mapOf())
        recyclerView.adapter = adapter

        // Apply StickyHeaderItemDecoration to keep the header at the top while scrolling
        val stickyHeaderDecoration = StickyHeaderItemDecoration(adapter)
        recyclerView.addItemDecoration(stickyHeaderDecoration)

        // Initialize the ViewModel to observe the data
        val repository = ItemRepository()
        val factory = ItemViewModelFactory(application, repository)  // Pass the application context
        viewModel = ViewModelProvider(this, factory)[ItemViewModel::class.java]


        // Observe the LiveData for grouped items from the ViewModel
        viewModel.items.observe(this) { groupedItems ->
            // Update the adapter with the new grouped items
            if (groupedItems != null) {
                adapter.updateData(groupedItems)
            }
        }

        // Trigger the ViewModel to fetch the items
        viewModel.fetchItems()
    }
}





