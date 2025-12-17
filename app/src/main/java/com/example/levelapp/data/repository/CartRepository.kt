package com.example.levelapp.data.repository

import android.content.Context
import com.example.levelapp.model.CartItem
import com.example.levelapp.model.Order
import com.example.levelapp.model.OrderItemRequest
import com.example.levelapp.model.OrderRequest
import com.example.levelapp.data.network.RetrofitClient

class CartRepository(context: Context) {
    private val api = RetrofitClient.backendService
    companion object {
        private val cartItems = mutableListOf<CartItem>()
    }

    suspend fun getCartItems(): List<CartItem> {
        return cartItems
    }

    suspend fun addToCart(item: CartItem) {
        val index = cartItems.indexOfFirst { it.productId == item.productId }
        if (index != -1) {
            cartItems[index] = item
        } else {
            cartItems.add(item)
        }
    }

    suspend fun removeFromCart(productId: Long) {
        cartItems.removeAll { it.productId == productId }
    }

    suspend fun clearCart() {
        cartItems.clear()
    }

    suspend fun sendOrder(userId: Long): Result<Order> {
        val requestItems = cartItems.map {
            OrderItemRequest(productId = it.productId, quantity = it.quantity)
        }
        val request = OrderRequest(userId = userId, items = requestItems)

        return try {
            val response = api.createOrder(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear pedido: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
