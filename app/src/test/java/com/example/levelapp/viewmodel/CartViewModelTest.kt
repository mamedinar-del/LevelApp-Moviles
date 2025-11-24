package com.example.levelapp.viewmodel

import android.app.Application
import com.example.levelapp.model.CartItem
import com.example.levelapp.util.MainDispatcherRule
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val application = mockk<Application>(relaxed = true)

    @Test
    fun `calcular total del carrito es correcto`() {

        val item1 = CartItem(1, 1, "PS5", 500.0, "", 2)
        val item2 = CartItem(2, 2, "Juego", 50.0, "", 1)

        val lista = listOf(item1, item2)

        val total = lista.sumOf { it.precio * it.quantity }

        assertEquals(1050.0, total, 0.0)
    }
}