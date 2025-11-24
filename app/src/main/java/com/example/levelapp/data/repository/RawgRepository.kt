package com.example.levelapp.data.repository

import com.example.levelapp.data.network.RetrofitClient
import com.example.levelapp.model.RawgGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RawgRepository {
    private val API_KEY = "77c4f5e360564820bb1d0225db3ec76c"

    suspend fun searchGames(query: String): List<RawgGame> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.apiService.searchGames(API_KEY, query)
            if (response.isSuccessful) {
                response.body()?.results ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}