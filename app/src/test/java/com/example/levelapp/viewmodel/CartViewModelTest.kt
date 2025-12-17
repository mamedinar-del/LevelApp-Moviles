package com.example.levelapp.viewmodel

import android.app.Application
import com.example.levelapp.data.repository.CartRepository
import com.example.levelapp.model.Product
import com.example.levelapp.util.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: CartViewModel

    private val repository = CartRepository(application)

    @Before
    fun setup() = runTest {
        repository.clearCart()

        viewModel = CartViewModel(application)
    }

    @Test
    fun `addToCart agrega un producto nuevo al carrito`() = runTest {
        val producto = Product(1, "Monitor", "4K", 10, 200.0, "Tech", "img")

        viewModel.addToCart(producto)
        advanceUntilIdle()

        val items = viewModel.uiState.value.cartItems
        assertEquals(1, items.size)
        assertEquals("Monitor", items[0].nombre)
    }

    @Test
    fun `addToCart incrementa cantidad si el producto ya existe`() = runTest {
        val producto = Product(1, "Monitor", "4K", 10, 200.0, "Tech", "img")

        viewModel.addToCart(producto)
        advanceUntilIdle()

        viewModel.addToCart(producto)
        advanceUntilIdle()

        val items = viewModel.uiState.value.cartItems
        assertEquals(1, items.size)
        assertEquals(2, items[0].quantity)
    }

    @Test
    fun `removeFromCart elimina el producto`() = runTest {
        val producto = Product(1, "Monitor", "4K", 10, 200.0, "Tech", "img")
        viewModel.addToCart(producto)
        advanceUntilIdle()

        viewModel.removeFromCart(1)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.cartItems.isEmpty())
    }

    @Test
    fun `increaseQuantity y decreaseQuantity actualizan valores`() = runTest {
        val producto = Product(1, "Mouse", "", 10, 50.0, "", "")
        viewModel.addToCart(producto)
        advanceUntilIdle()

        val item = viewModel.uiState.value.cartItems.first()

        viewModel.increaseQuantity(item)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.cartItems.first().quantity)

        viewModel.decreaseQuantity(viewModel.uiState.value.cartItems.first())
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.cartItems.first().quantity)
    }

    @Test
    fun `calcular total del carrito es correcto`() = runTest {
        val p1 = Product(1, "A", "", 10, 100.0, "", "")
        val p2 = Product(2, "B", "", 10, 50.0, "", "")

        viewModel.addToCart(p1)
        viewModel.addToCart(p1)
        viewModel.addToCart(p2)
        advanceUntilIdle()

        assertEquals(250.0, viewModel.uiState.value.total, 0.0)
    }
}