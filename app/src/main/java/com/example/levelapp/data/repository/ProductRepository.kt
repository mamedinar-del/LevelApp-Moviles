package com.example.levelapp.data.repository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.levelapp.data.local.DatabaseHelper
import com.example.levelapp.data.network.RetrofitClient
import com.example.levelapp.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.backendService.getProductsFromBackend()
            if (response.isSuccessful && response.body() != null) {
                val productosServer = response.body()!!
                val db = dbHelper.writableDatabase
                db.beginTransaction()
                try {
                    db.delete(DatabaseHelper.TABLE_PRODUCTS, null, null)
                    for (p in productosServer) {
                        val values = ContentValues().apply {
                            put(DatabaseHelper.COLUMN_PRODUCT_ID, p.id)
                            put(DatabaseHelper.COLUMN_PRODUCT_NAME, p.nombre)
                            put(DatabaseHelper.COLUMN_PRODUCT_DESC, p.descripcion)
                            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, p.stock)
                            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, p.precio)
                            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, p.categoria)
                            put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI, p.imagenUri)
                        }
                        db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values)
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }
        } catch (e: Exception) {
            Log.e("API_TEST", "Error sync: ${e.message}")
        }

        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, null, null, null, null, null)
        val products = mutableListOf<Product>()
        while (cursor.moveToNext()) {
            products.add(Product(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESC)),
                stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)),
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
                categoria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)),
                imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI))
            ))
        }
        cursor.close()
        products
    }

    suspend fun addProduct(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.nombre)
            put(DatabaseHelper.COLUMN_PRODUCT_DESC, product.descripcion)
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, product.stock)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.precio)
            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.categoria)
            put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI, product.imagenUri)
        }
        val newRowId = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values)

        try {
            RetrofitClient.backendService.createProductInBackend(product)
        } catch (e: Exception) {
            Log.e("API_TEST", "Error subida: ${e.message}")
        }

        if (newRowId == -1L) Result.failure(Exception("Error local")) else Result.success(Unit)
    }

    suspend fun updateProduct(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            RetrofitClient.backendService.updateProductInBackend(product.id, product)
        } catch (e: Exception) {
            Log.e("API_TEST", "Error update remoto: ${e.message}")
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.nombre)
            put(DatabaseHelper.COLUMN_PRODUCT_DESC, product.descripcion)
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, product.stock)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.precio)
            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.categoria)
            put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI, product.imagenUri)
        }
        val rows = db.update(DatabaseHelper.TABLE_PRODUCTS, values, "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?", arrayOf(product.id.toString()))

        if (rows > 0) Result.success(Unit) else Result.failure(Exception("Error local"))
    }

    suspend fun deleteProduct(productId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            RetrofitClient.backendService.deleteProductInBackend(productId)
        } catch (e: Exception) {
            Log.e("API_TEST", "Error delete remoto: ${e.message}")
        }

        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_PRODUCTS, "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?", arrayOf(productId.toString()))
        Result.success(Unit)
    }

    suspend fun searchProducts(query: String): List<Product> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, "${DatabaseHelper.COLUMN_PRODUCT_NAME} LIKE ?", arrayOf("%$query%"), null, null, null)
        val list = mutableListOf<Product>()
        while (cursor.moveToNext()) {
            list.add(Product(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESC)),
                stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)),
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
                categoria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)),
                imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI))
            ))
        }
        cursor.close()
        list
    }
}