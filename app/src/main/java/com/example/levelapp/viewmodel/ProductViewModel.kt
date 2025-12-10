package com.example.levelapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelapp.data.repository.ProductRepository
import com.example.levelapp.data.repository.RawgRepository
import com.example.levelapp.model.Product
import com.example.levelapp.model.RawgGame
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
    val productosFiltrados: List<Product> = emptyList(),
    val searchText: String = "",
    val idEdicion: Long? = null,
    val busquedaApiQuery: String = "",
    val resultadosApi: List<RawgGame> = emptyList(),
    val buscandoApi: Boolean = false,
    val mostrarDialogoApi: Boolean = false
)

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProductRepository(application)
    private val rawgRepository = RawgRepository()

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v) }
    fun onDescripcionChange(v: String) = _uiState.update { it.copy(descripcion = v) }
    fun onStockChange(v: String) = _uiState.update { it.copy(stock = v) }
    fun onPrecioChange(v: String) = _uiState.update { it.copy(precio = v) }
    fun onCategoriaChange(v: String) = _uiState.update { it.copy(categoria = v) }
    fun onImagenUriChange(uri: Uri?) { _uiState.update { it.copy(imagenUri = uri) } }

    fun onSearchTextChange(text: String) {
        _uiState.update { state ->
            val filtrados = if (text.isBlank()) state.productos else state.productos.filter { it.nombre.contains(text, ignoreCase = true) }
            state.copy(searchText = text, productosFiltrados = filtrados)
        }
    }

    fun cargarProductos() = viewModelScope.launch {
        val lista = repository.getAllProducts()
        _uiState.update { state ->
            state.copy(
                productos = lista,
                productosFiltrados = if (state.searchText.isBlank()) lista else lista.filter { it.nombre.contains(state.searchText, true) }
            )
        }
    }

    fun guardarProducto() {
        val s = _uiState.value
        viewModelScope.launch {
            val prod = Product(
                id = s.idEdicion ?: 0,
                nombre = s.nombre,
                descripcion = s.descripcion,
                stock = s.stock.toIntOrNull() ?: 0,
                precio = s.precio.toDoubleOrNull() ?: 0.0,
                categoria = s.categoria,
                imagenUri = s.imagenUri.toString()
            )

            if (s.idEdicion == null) {
                repository.addProduct(prod)
                    .onSuccess {
                        cargarProductos()
                        _uiState.update { it.copy(mensaje = "Producto Creado", nombre = "", descripcion = "", stock = "", precio = "", categoria = "", imagenUri = null) }
                    }
                    .onFailure { e -> _uiState.update { it.copy(mensaje = "Error: ${e.message}") } }
            } else {
                repository.updateProduct(prod)
                    .onSuccess {
                        cargarProductos()
                        _uiState.update { it.copy(mensaje = "Producto Actualizado", nombre = "", descripcion = "", stock = "", precio = "", categoria = "", imagenUri = null, idEdicion = null) }
                    }
                    .onFailure { e -> _uiState.update { it.copy(mensaje = "Error: ${e.message}") } }
            }
        }
    }

    fun eliminarProducto(producto: Product) {
        viewModelScope.launch {
            repository.deleteProduct(producto.id)
                .onSuccess {
                    cargarProductos()
                    _uiState.update { it.copy(mensaje = "Eliminado") }
                }
                .onFailure { e -> _uiState.update { it.copy(mensaje = "Error: ${e.message}") } }
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
        _uiState.update { it.copy(nombre = "", descripcion = "", stock = "", precio = "", categoria = "", imagenUri = null, mensaje = null, idEdicion = null) }
    }

    fun toggleDialogoApi() { _uiState.update { it.copy(mostrarDialogoApi = !it.mostrarDialogoApi) } }
    fun buscarEnApi(query: String) {
        _uiState.update { it.copy(busquedaApiQuery = query, buscandoApi = true) }
        viewModelScope.launch {
            val resultados = rawgRepository.searchGames(query)
            _uiState.update { it.copy(resultadosApi = resultados, buscandoApi = false) }
        }
    }
    fun seleccionarJuegoApi(juego: RawgGame) {
        _uiState.update {
            it.copy(nombre = juego.name, descripcion = "Lanzado: ${juego.released}", categoria = "Videojuego", imagenUri = if (juego.backgroundImage != null) Uri.parse(juego.backgroundImage) else null, mostrarDialogoApi = false)
        }
    }
}