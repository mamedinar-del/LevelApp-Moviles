package com.example.levelapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelapp.data.repository.ProductRepository
import com.example.levelapp.model.Product
import com.example.levelapp.ui.utils.copiarImagenAInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProductUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val stock: String = "",
    val precio: String = "",
    val categoria: String = "",
    val imagenUri: Uri? = null,
    val mensaje: String? = null,

    val productos: List<Product> = emptyList(),

    val searchText: String = "",
    val productosFiltrados: List<Product> = emptyList(),

    val idEdicion: Long? = null
)

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProductRepository(application)
    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init { cargarProductos() }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v) }
    fun onDescripcionChange(v: String) = _uiState.update { it.copy(descripcion = v) }
    fun onStockChange(v: String) = _uiState.update { it.copy(stock = v) }
    fun onPrecioChange(v: String) = _uiState.update { it.copy(precio = v) }
    fun onCategoriaChange(v: String) = _uiState.update { it.copy(categoria = v) }

    fun onImagenUriChange(uri: Uri?) {
        if (uri == null) { _uiState.update { it.copy(imagenUri = null) }; return }
        viewModelScope.launch(Dispatchers.IO) {
            val ruta = copiarImagenAInternalStorage(getApplication(), uri)
            if (ruta != null) _uiState.update { it.copy(imagenUri = Uri.parse("file://$ruta")) }
        }
    }

    fun onSearchTextChange(text: String) {
        _uiState.update { state ->
            val filtrados = if (text.isBlank()) {
                state.productos
            } else {
                state.productos.filter { it.nombre.contains(text, ignoreCase = true) }
            }
            state.copy(searchText = text, productosFiltrados = filtrados)
        }
    }


    fun empezarEdicion(producto: Product) {
        _uiState.update {
            it.copy(
                idEdicion = producto.id,
                nombre = producto.nombre,
                descripcion = producto.descripcion,
                stock = producto.stock.toString(),
                precio = producto.precio.toString(),
                categoria = producto.categoria,
                imagenUri = Uri.parse(producto.imagenUri),
                mensaje = "Editando: ${producto.nombre}"
            )
        }
    }

    fun cancelarEdicion() {
        limpiarForm()
    }

    fun guardarProducto() {
        val s = _uiState.value
        if (s.nombre.isBlank() || s.imagenUri == null) { _uiState.update { it.copy(mensaje = "Faltan datos") }; return }

        viewModelScope.launch {
            if (s.idEdicion == null) {
                val p = Product(0, s.nombre, s.descripcion, s.stock.toIntOrNull()?:0, s.precio.toDoubleOrNull()?:0.0, s.categoria, s.imagenUri.toString())
                repository.addProduct(p).onSuccess { cargarProductos(); limpiarForm() }
            } else {
                val p = Product(s.idEdicion, s.nombre, s.descripcion, s.stock.toIntOrNull()?:0, s.precio.toDoubleOrNull()?:0.0, s.categoria, s.imagenUri.toString())
                repository.updateProduct(p).onSuccess {
                    cargarProductos()
                    limpiarForm()
                    _uiState.update { it.copy(mensaje = "Producto Actualizado") }
                }
            }
        }
    }

    fun cargarProductos() = viewModelScope.launch {
        val lista = repository.getAllProducts()
        _uiState.update {
            it.copy(
                productos = lista,
                productosFiltrados = if (it.searchText.isBlank()) lista else lista.filter { p -> p.nombre.contains(it.searchText, true) }
            )
        }
    }

    private fun limpiarForm() = _uiState.update { it.copy(nombre = "", descripcion = "", stock = "", precio = "", categoria = "", imagenUri = null, mensaje = null, idEdicion = null) }
}