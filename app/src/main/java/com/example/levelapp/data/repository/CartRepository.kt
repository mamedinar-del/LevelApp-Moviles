package com.example.levelapp.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.levelapp.data.local.DatabaseHelper
import com.example.levelapp.model.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.database.sqlite.SQLiteDatabase

class CartRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    suspend fun getCartItems(): List<CartItem> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_CART, null, null, null, null, null, null)
        val items = mutableListOf<CartItem>()
        while (cursor.moveToNext()) {
            items.add(
                CartItem(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_ID)),
                    productId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_PRODUCT_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_PRODUCT_NAME)),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_PRODUCT_PRICE)),
                    imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_PRODUCT_IMAGE_URI)),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_QUANTITY))
                )
            )
        }
        cursor.close()
        items
    }

    suspend fun addToCart(item: CartItem) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CART_PRODUCT_ID, item.productId)
            put(DatabaseHelper.COLUMN_CART_PRODUCT_NAME, item.nombre)
            put(DatabaseHelper.COLUMN_CART_PRODUCT_PRICE, item.precio)
            put(DatabaseHelper.COLUMN_CART_PRODUCT_IMAGE_URI, item.imagenUri)
            put(DatabaseHelper.COLUMN_CART_QUANTITY, item.quantity)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_CART, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    suspend fun removeFromCart(productId: Long) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_CART, "${DatabaseHelper.COLUMN_CART_PRODUCT_ID} = ?", arrayOf(productId.toString()))
    }
}