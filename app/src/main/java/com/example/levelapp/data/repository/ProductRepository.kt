package com.example.levelapp.data.repository

import android.content.Context
import android.util.Log
import com.example.levelapp.data.network.RetrofitClient
import com.example.levelapp.model.Product

class ProductRepository(context: Context) {

    private val api = RetrofitClient.backendService

    suspend fun getAllProducts(): List<Product> {
        return try {
            val response = api.getProductsFromBackend()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("ProductRepo", "Error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductRepo", "Fallo conexión: ${e.message}")
            emptyList()
        }
    }

    suspend fun addProduct(product: Product): Result<Product> {
        return try {
            val response = api.createProductInBackend(product)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Product> {
        return try {
            val response = api.updateProductInBackend(product.id, product)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: Long): Result<Boolean> {
        return try {
            val response = api.deleteProductInBackend(id)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al eliminar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}