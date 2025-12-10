package com.example.levelapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.levelapp.model.Product

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "levelup.db"
        private const val DATABASE_VERSION = 8

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_NAME = "nombre"
        const val COLUMN_LASTNAME = "apellido"
        const val COLUMN_RUT = "rut"
        const val COLUMN_PROFILE_IMAGE = "imagen_perfil"

        const val TABLE_PRODUCTS = "products"
        const val COLUMN_PRODUCT_ID = "id"
        const val COLUMN_PRODUCT_NAME = "nombre"
        const val COLUMN_PRODUCT_DESC = "descripcion"
        const val COLUMN_PRODUCT_STOCK = "stock"
        const val COLUMN_PRODUCT_PRICE = "precio"
        const val COLUMN_PRODUCT_CATEGORY = "categoria"
        const val COLUMN_PRODUCT_IMAGE_URI = "imagen_uri"

        const val TABLE_CART = "cart_items"
        const val COLUMN_CART_ID = "id"
        const val COLUMN_CART_PRODUCT_ID = "product_id"
        const val COLUMN_CART_PRODUCT_NAME = "nombre"
        const val COLUMN_CART_PRODUCT_PRICE = "precio"
        const val COLUMN_CART_PRODUCT_IMAGE_URI = "imagen_uri"
        const val COLUMN_CART_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_USERS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_EMAIL TEXT UNIQUE, $COLUMN_PASSWORD TEXT, $COLUMN_NAME TEXT, $COLUMN_LASTNAME TEXT, $COLUMN_RUT TEXT, $COLUMN_PROFILE_IMAGE TEXT)")

        db.execSQL("CREATE TABLE $TABLE_PRODUCTS ($COLUMN_PRODUCT_ID INTEGER PRIMARY KEY, $COLUMN_PRODUCT_NAME TEXT, $COLUMN_PRODUCT_DESC TEXT, $COLUMN_PRODUCT_STOCK INTEGER, $COLUMN_PRODUCT_PRICE REAL, $COLUMN_PRODUCT_CATEGORY TEXT, $COLUMN_PRODUCT_IMAGE_URI TEXT)")

        db.execSQL("CREATE TABLE $TABLE_CART ($COLUMN_CART_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_CART_PRODUCT_ID INTEGER UNIQUE, $COLUMN_CART_PRODUCT_NAME TEXT, $COLUMN_CART_PRODUCT_PRICE REAL, $COLUMN_CART_PRODUCT_IMAGE_URI TEXT, $COLUMN_CART_QUANTITY INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        onCreate(db)
    }


    fun addProduct(product: Product): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            if (product.id > 0) put(COLUMN_PRODUCT_ID, product.id)
            put(COLUMN_PRODUCT_NAME, product.nombre)
            put(COLUMN_PRODUCT_DESC, product.descripcion)
            put(COLUMN_PRODUCT_STOCK, product.stock)
            put(COLUMN_PRODUCT_PRICE, product.precio)
            put(COLUMN_PRODUCT_CATEGORY, product.categoria)
            put(COLUMN_PRODUCT_IMAGE_URI, product.imagenUri)
        }
        val id = db.insertWithOnConflict(TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return id
    }

    fun getAllProducts(): List<Product> {
        val productList = ArrayList<Product>()
        val selectQuery = "SELECT * FROM $TABLE_PRODUCTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESC)),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_STOCK)),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                    categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                    imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_URI))
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }
}