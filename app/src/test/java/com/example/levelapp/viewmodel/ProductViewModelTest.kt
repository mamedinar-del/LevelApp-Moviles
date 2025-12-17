package com.example.levelapp.viewmodel

import android.app.Application
import com.example.levelapp.data.repository.ProductRepository
import com.example.levelapp.data.repository.RawgRepository
import com.example.levelapp.model.Product
import com.example.levelapp.model.RawgGame
import com.example.levelapp.util.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private val repository = mockk<ProductRepository>()
    private val rawgRepository = mockk<RawgRepository>()

    private lateinit var viewModel: ProductViewModel

    @Before
    fun setup() {
        coEvery { repository.getAllProducts() } returns emptyList()
    }

    @Test
    fun `cargarProductos actualiza el estado con la lista del repositorio`() = runTest {
        val listaPrueba = listOf(
            Product(1, "Mario", "Juego", 10, 500.0, "Accion", ""),
            Product(2, "Zelda", "Aventura", 5, 900.0, "RPG", "")
        )
        coEvery { repository.getAllProducts() } returns listaPrueba

        viewModel = ProductViewModel(application, repository, rawgRepository)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.productos.size)
        assertEquals("Mario", viewModel.uiState.value.productos[0].nombre)
    }

    @Test
    fun `onSearchTextChange filtra correctamente la lista de productos`() = runTest {
        val p1 = Product(1, "Fifa 24", "", 0, 0.0, "", "")
        val p2 = Product(2, "Final Fantasy", "", 0, 0.0, "", "")
        coEvery { repository.getAllProducts() } returns listOf(p1, p2)

        viewModel = ProductViewModel(application, repository, rawgRepository)
        advanceUntilIdle()

        viewModel.onSearchTextChange("Fifa")

        assertEquals("Fifa", viewModel.uiState.value.searchText)
        assertEquals(1, viewModel.uiState.value.productosFiltrados.size)
        assertEquals("Fifa 24", viewModel.uiState.value.productosFiltrados.first().nombre)
    }

    @Test
    fun `guardarProducto (Crear) llama a addProduct y limpia campos en exito`() = runTest {
        viewModel = ProductViewModel(application, repository, rawgRepository)
        viewModel.onNombreChange("Nuevo Juego")
        viewModel.onPrecioChange("1000")
        viewModel.onStockChange("5")

        val productoCreado = Product(1, "Nuevo Juego", "", 5, 1000.0, "", "")
        coEvery { repository.addProduct(any()) } returns Result.success(productoCreado)

        coEvery { repository.getAllProducts() } returns emptyList()

        viewModel.guardarProducto()
        advanceUntilIdle()

        coVerify { repository.addProduct(any()) }
        assertEquals("Producto Creado", viewModel.uiState.value.mensaje)
        assertEquals("", viewModel.uiState.value.nombre)
    }

    @Test
    fun `eliminarProducto llama a deleteProduct`() = runTest {
        viewModel = ProductViewModel(application, repository, rawgRepository)
        val producto = Product(5, "Borrar", "", 0, 0.0, "", "")

        coEvery { repository.deleteProduct(5) } returns Result.success(true)

        coEvery { repository.getAllProducts() } returns emptyList()

        viewModel.eliminarProducto(producto)
        advanceUntilIdle()


        coVerify { repository.deleteProduct(5) }
        assertEquals("Eliminado", viewModel.uiState.value.mensaje)
    }
}