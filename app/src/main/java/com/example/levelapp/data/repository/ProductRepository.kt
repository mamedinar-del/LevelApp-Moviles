package com.example.levelapp.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.levelapp.data.local.DatabaseHelper
import com.example.levelapp.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

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
        if (newRowId == -1L) {
            Result.failure(Exception("Error al agregar el producto."))
        } else {
            Result.success(Unit)
        }
    }

    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, null, null, null, null, null)
        val products = mutableListOf<Product>()
        while (cursor.moveToNext()) {
            products.add(
                Product(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESC)),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
                    categoria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)),
                    imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI))
                )
            )
        }
        cursor.close()
        products
    }
}