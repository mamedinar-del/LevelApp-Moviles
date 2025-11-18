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
    val nombre: String = "", val descripcion: String = "", val stock: String = "",
    val precio: String = "", val categoria: String = "", val imagenUri: Uri? = null,
    val mensaje: String? = null, val productos: List<Product> = emptyList()
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
        if (uri == null) {
            _uiState.update { it.copy(imagenUri = null) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val rutaPermanente = copiarImagenAInternalStorage(getApplication(), uri)

            if (rutaPermanente != null) {
                val uriFile = Uri.parse("file://$rutaPermanente")
                _uiState.update { it.copy(imagenUri = uriFile) }
            }
        }
    }

    fun agregarProducto() {
        val s = _uiState.value
        if (s.nombre.isBlank() || s.imagenUri == null) { _uiState.update { it.copy(mensaje = "Datos incompletos") }; return }

        viewModelScope.launch {
            val p = Product(0, s.nombre, s.descripcion, s.stock.toIntOrNull()?:0, s.precio.toDoubleOrNull()?:0.0, s.categoria, s.imagenUri.toString())
            repository.addProduct(p).onSuccess { cargarProductos(); limpiarForm() }
        }
    }

    fun cargarProductos() = viewModelScope.launch { _uiState.update { it.copy(productos = repository.getAllProducts()) } }
    private fun limpiarForm() = _uiState.update { it.copy(nombre = "", descripcion = "", stock = "", precio = "", categoria = "", imagenUri = null, mensaje = "Agregado") }
}