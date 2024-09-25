package com.example.fetchapp.model

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItemRepository {

    private val apiService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fetch-hiring.s3.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }
    // Function to fetch items from the API
    fun fetchItems(): Call<List<Item>> {
        return apiService.getItems() // Ensure that getItems() returns Call<List<Item>>
    }


}



