package com.example.levelapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelapp.data.repository.ProductRepository
import com.example.levelapp.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val stock: String = "",
    val precio: String = "",
    val categoria: String = "",
    val imagenUri: Uri? = null,
    val mensaje: String? = null,
    val productos: List<Product> = emptyList()
)

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepository = ProductRepository(application)

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    fun onNombreChange(nombre: String) { _uiState.update { it.copy(nombre = nombre) } }
    fun onDescripcionChange(desc: String) { _uiState.update { it.copy(descripcion = desc) } }
    fun onStockChange(stock: String) { _uiState.update { it.copy(stock = stock) } }
    fun onPrecioChange(precio: String) { _uiState.update { it.copy(precio = precio) } }
    fun onCategoriaChange(cat: String) { _uiState.update { it.copy(categoria = cat) } }
    fun onImagenUriChange(uri: Uri?) { _uiState.update { it.copy(imagenUri = uri) } }

    fun agregarProducto() {
        // Validación básica
        if (_uiState.value.nombre.isBlank() || _uiState.value.stock.isBlank() || _uiState.value.precio.isBlank() || _uiState.value.categoria.isBlank() || _uiState.value.imagenUri == null) {
            _uiState.update { it.copy(mensaje = "Todos los campos son obligatorios.") }
            return
        }

        viewModelScope.launch {
            val product = Product(
                nombre = _uiState.value.nombre,
                descripcion = _uiState.value.descripcion,
                stock = _uiState.value.stock.toIntOrNull() ?: 0,
                precio = _uiState.value.precio.toDoubleOrNull() ?: 0.0,
                categoria = _uiState.value.categoria,
                imagenUri = _uiState.value.imagenUri.toString()
            )
            val result = productRepository.addProduct(product)
            result.onSuccess {
                limpiarFormulario()
                cargarProductos()
            }.onFailure { exception ->
                _uiState.update { it.copy(mensaje = exception.message) }
            }
        }
    }

    fun cargarProductos() {
        viewModelScope.launch {
            val productList = productRepository.getAllProducts()
            _uiState.update { it.copy(productos = productList) }
        }
    }

    private fun limpiarFormulario() {
        _uiState.update { it.copy(
            nombre = "", descripcion = "", stock = "", precio = "", categoria = "", imagenUri = null, mensaje = "¡Producto agregado con éxito!"
        )}
    }
}