package com.example.levelapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.levelapp.model.Product

class ProductDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addProduct(product: Product): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            if (product.id > 0) put(DatabaseHelper.COLUMN_PRODUCT_ID, product.id)
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.nombre)
            put(DatabaseHelper.COLUMN_PRODUCT_DESC, product.descripcion)
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, product.stock)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.precio)
            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.categoria)
            put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI, product.imagenUri)
        }
        val id = db.insertWithOnConflict(DatabaseHelper.TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return id
    }

    fun getAllProducts(): List<Product> {
        val productList = ArrayList<Product>()
        val selectQuery = "SELECT * FROM ${DatabaseHelper.TABLE_PRODUCTS}"
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESC)),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
                    categoria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)),
                    imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI))
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    fun updateProduct(product: Product): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.nombre)
            put(DatabaseHelper.COLUMN_PRODUCT_DESC, product.descripcion)
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, product.stock)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.precio)
            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.categoria)
            put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI, product.imagenUri)
        }
        val success = db.update(
            DatabaseHelper.TABLE_PRODUCTS,
            values,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(product.id.toString())
        )
        db.close()
        return success
    }

    fun deleteProduct(id: Long): Int {
        val db = dbHelper.writableDatabase
        val success = db.delete(
            DatabaseHelper.TABLE_PRODUCTS,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return success
    }
}