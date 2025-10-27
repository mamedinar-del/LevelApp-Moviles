package com.example.levelapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelapp.data.repository.CartRepository
import com.example.levelapp.model.CartItem
import com.example.levelapp.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val total: Double = 0.0
)

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val cartRepository = CartRepository(application)

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCartItems()
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val currentItems = _uiState.value.cartItems
            val existingItem = currentItems.find { it.productId == product.id }

            val newItem = if (existingItem != null) {
                existingItem.copy(quantity = existingItem.quantity + 1)
            } else {
                CartItem(
                    productId = product.id,
                    nombre = product.nombre,
                    precio = product.precio,
                    imagenUri = product.imagenUri,
                    quantity = 1
                )
            }
            cartRepository.addToCart(newItem)
            loadCartItems()
        }
    }

    fun removeFromCart(productId: Long) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
            loadCartItems()
        }
    }

    fun loadCartItems() {
        viewModelScope.launch {
            val items = cartRepository.getCartItems()
            val totalPrice = items.sumOf { it.precio * it.quantity }
            _uiState.update { it.copy(cartItems = items, total = totalPrice) }
        }
    }
}