package com.example.fetchapp.viewmodel
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchapp.model.Item
import com.example.fetchapp.model.ItemRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class ItemViewModel(application: Application, private val repository: ItemRepository) : AndroidViewModel(application) {
    private val _items = MutableLiveData<Map<Int, List<Item>>?>()
    val items: MutableLiveData<Map<Int, List<Item>>?> = _items

    private var cachedItems: Map<Int, List<Item>>? = null

    fun fetchItems() {
        if (cachedItems != null) {
            _items.postValue(cachedItems)
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.fetchItems().awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let { itemList ->
                        val processedItems = itemList
                            .filter { !it.name.isNullOrBlank() }
                            .sortedWith(compareBy({ it.listId }, { extractNumberFromName(it.name) }))
                            .groupBy { it.listId }
                        cachedItems = processedItems
                        _items.postValue(processedItems)
                    }
                }
            } catch (e: Exception) {
                Log.e("ItemViewModel", "Error: ${e.message}")
                Toast.makeText(getApplication(), "Error fetching data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractNumberFromName(name: String?): Int {
        return name?.filter { it.isDigit() }?.toIntOrNull() ?: 0
    }
}


