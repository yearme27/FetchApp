package com.example.fetchapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.fetchapp.model.Item
import com.example.fetchapp.model.ItemRepository
import com.example.fetchapp.viewmodel.ItemViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var observer: Observer<Map<Int, List<Item>>?>  // Update Observer type

    @Mock
    private lateinit var call: Call<List<Item>>

    @Mock
    private lateinit var repository: ItemRepository

    private lateinit var viewModel: ItemViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = ItemViewModel(repository)
    }

    @Test
    fun testFetchItems_success() {
        val items = listOf(Item(1, 1, "Item 1"), Item(2, 1, "Item 2"))
        val response = Response.success(items)

        // Mock the callback to trigger success
        doAnswer {
            val callback: Callback<List<Item>> = it.getArgument(0)
            callback.onResponse(call, response)
        }.`when`(call).enqueue(any())

        `when`(repository.fetchItems()).thenReturn(call)

        viewModel.items.observeForever(observer)
        viewModel.fetchItems()

        // Make sure that items are posted correctly
        assertNotNull(viewModel.items.value)
        assertEquals(1, viewModel.items.value?.size)  // Size of grouped map
        assertEquals(2, viewModel.items.value?.get(1)?.size)  // Check number of items under listId 1
    }

    @Test
    fun testFetchItems_failure() {
        // Mock the callback to trigger failure
        doAnswer {
            val callback: Callback<List<Item>> = it.getArgument(0)
            callback.onFailure(call, Throwable("Network Error"))
        }.`when`(call).enqueue(any())

        `when`(repository.fetchItems()).thenReturn(call)

        viewModel.fetchItems()

        // Ensure that LiveData emits null on failure
        assertNull(viewModel.items.value)
    }
}


