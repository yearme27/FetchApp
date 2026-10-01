package com.example.fetchapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fetchapp.model.Item
import com.example.fetchapp.model.ItemRepository
import com.example.fetchapp.viewmodel.ItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ItemViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var call: Call<List<Item>>

    @Mock
    private lateinit var repository: ItemRepository

    private lateinit var viewModel: ItemViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // viewModelScope runs on Dispatchers.Main, which doesn't exist in plain unit tests.
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ItemViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // awaitResponse() enqueues the call, so the test answers enqueue() with the given callback action.
    private fun answerWith(action: (Callback<List<Item>>) -> Unit) {
        doAnswer {
            action(it.getArgument(0))
            null
        }.`when`(call).enqueue(any())
        `when`(repository.fetchItems()).thenReturn(call)
    }

    @Test
    fun fetchItems_groupsSortsAndDropsBlankNames() {
        val items = listOf(
            Item(3, 2, "Item 10"),
            Item(1, 1, "Item 2"),
            Item(2, 1, "Item 1"),
            Item(4, 1, ""),
            Item(5, 1, null)
        )
        answerWith { it.onResponse(call, Response.success(items)) }

        viewModel.fetchItems()

        val result = viewModel.items.value
        assertNotNull(result)
        assertEquals(listOf(1, 2), result!!.keys.toList())
        assertEquals(listOf("Item 1", "Item 2"), result[1]!!.map { it.name })
        assertEquals(listOf("Item 10"), result[2]!!.map { it.name })
        assertNull(viewModel.error.value)
    }

    @Test
    fun fetchItems_networkFailure_postsErrorAndNoItems() {
        answerWith { it.onFailure(call, IOException("Network Error")) }

        viewModel.fetchItems()

        assertNull(viewModel.items.value)
        assertNotNull(viewModel.error.value)
    }

    @Test
    fun fetchItems_serverError_postsErrorWithStatusCode() {
        val errorResponse = Response.error<List<Item>>(500, ResponseBody.create(null, ""))
        answerWith { it.onResponse(call, errorResponse) }

        viewModel.fetchItems()

        assertNull(viewModel.items.value)
        assertTrue(viewModel.error.value!!.contains("500"))
    }

    @Test
    fun fetchItems_secondCallUsesCache() {
        answerWith { it.onResponse(call, Response.success(listOf(Item(1, 1, "Item 1")))) }

        viewModel.fetchItems()
        viewModel.fetchItems()

        verify(repository, times(1)).fetchItems()
        assertEquals(1, viewModel.items.value?.size)
    }

    @Test
    fun onErrorShown_clearsTheError() {
        answerWith { it.onFailure(call, IOException("Network Error")) }
        viewModel.fetchItems()

        viewModel.onErrorShown()

        assertNull(viewModel.error.value)
    }
}
