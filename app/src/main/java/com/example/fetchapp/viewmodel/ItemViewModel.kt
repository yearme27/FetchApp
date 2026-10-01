package com.example.fetchapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchapp.model.Item
import com.example.fetchapp.model.ItemRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class ItemViewModel(private val repository: ItemRepository) : ViewModel() {

    private val _items = MutableLiveData<Map<Int, List<Item>>?>()
    val items: LiveData<Map<Int, List<Item>>?> get() = _items

    // A message for the UI to show once. Call onErrorShown() after showing it.
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private var cachedItems: Map<Int, List<Item>>? = null

    fun fetchItems() {
        cachedItems?.let {
            _items.postValue(it)
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.fetchItems().awaitResponse()
                if (response.isSuccessful) {
                    val itemList = response.body().orEmpty()
                    val processedItems = itemList
                        .filter { !it.name.isNullOrBlank() }
                        .sortedWith(compareBy({ it.listId }, { extractNumberFromName(it.name) }))
                        .groupBy { it.listId }
                    cachedItems = processedItems
                    _items.value = processedItems
                } else {
                    Log.e("ItemViewModel", "Server error: ${response.code()}")
                    _error.value = "Couldn't load items (server returned ${response.code()})."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("ItemViewModel", "Error: ${e.message}")
                _error.value = "Couldn't load items. Check your connection and try again."
            }
        }
    }

    fun onErrorShown() {
        _error.value = null
    }

    private fun extractNumberFromName(name: String?): Int {
        return name?.filter { it.isDigit() }?.toIntOrNull() ?: 0
    }
}
