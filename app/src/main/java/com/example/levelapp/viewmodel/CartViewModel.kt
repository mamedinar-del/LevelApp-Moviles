package com.example.levelapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelapp.data.repository.CartRepository
import com.example.levelapp.model.CartItem
import com.example.levelapp.model.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val total: Double = 0.0
)

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CartRepository(application)
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCartItems()
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val existingItem = _uiState.value.cartItems.find { it.productId == product.id }
            val newItem = existingItem?.copy(quantity = existingItem.quantity + 1) ?: CartItem(
                id = 0,
                productId = product.id,
                nombre = product.nombre,
                precio = product.precio,
                imagenUri = product.imagenUri,
                quantity = 1
            )
            repository.addToCart(newItem)
            loadCartItems()
        }
    }


    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            val updatedItem = item.copy(quantity = item.quantity + 1)
            repository.addToCart(updatedItem)
            loadCartItems()
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            if (item.quantity > 1) {
                val updatedItem = item.copy(quantity = item.quantity - 1)
                repository.addToCart(updatedItem)
            } else {
                repository.removeFromCart(item.productId)
            }
            loadCartItems()
        }
    }


    fun removeFromCart(productId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            loadCartItems()
        }
    }

    fun loadCartItems() {
        viewModelScope.launch {
            val items = repository.getCartItems()
            _uiState.update {
                it.copy(
                    cartItems = items,
                    total = items.sumOf { item -> item.precio * item.quantity }
                )
            }
        }
    }
}