package com.example.levelapp.data.repository

import com.example.levelapp.data.network.RetrofitClient
import com.example.levelapp.model.Order

class OrderRepository {

    private val api = RetrofitClient.backendService

    suspend fun getAllOrders(): Result<List<Order>> {
        return try {
            val response = api.getAllOrders()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener pedidos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}