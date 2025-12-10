package com.example.levelapp.data.repository

import android.content.Context
import com.example.levelapp.model.CartItem

class CartRepository(context: Context) {

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
}
