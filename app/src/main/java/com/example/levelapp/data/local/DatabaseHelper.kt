package com.example.levelapp.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "levelup.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

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
        val createUserTableQuery = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_EMAIL TEXT UNIQUE,"
                + "$COLUMN_PASSWORD TEXT)")
        db.execSQL(createUserTableQuery)

        val createProductTableQuery = ("CREATE TABLE $TABLE_PRODUCTS ("
                + "$COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_PRODUCT_NAME TEXT,"
                + "$COLUMN_PRODUCT_DESC TEXT,"
                + "$COLUMN_PRODUCT_STOCK INTEGER,"
                + "$COLUMN_PRODUCT_PRICE REAL,"
                + "$COLUMN_PRODUCT_CATEGORY TEXT,"
                + "$COLUMN_PRODUCT_IMAGE_URI TEXT)")
        db.execSQL(createProductTableQuery)

        val createCartTableQuery = ("CREATE TABLE $TABLE_CART ("
                + "$COLUMN_CART_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_CART_PRODUCT_ID INTEGER UNIQUE,"
                + "$COLUMN_CART_PRODUCT_NAME TEXT,"
                + "$COLUMN_CART_PRODUCT_PRICE REAL,"
                + "$COLUMN_CART_PRODUCT_IMAGE_URI TEXT,"
                + "$COLUMN_CART_QUANTITY INTEGER)")
        db.execSQL(createCartTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createProductTableQuery = ("CREATE TABLE $TABLE_PRODUCTS ("
                    + "$COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$COLUMN_PRODUCT_NAME TEXT,"
                    + "$COLUMN_PRODUCT_DESC TEXT,"
                    + "$COLUMN_PRODUCT_STOCK INTEGER,"
                    + "$COLUMN_PRODUCT_PRICE REAL,"
                    + "$COLUMN_PRODUCT_CATEGORY TEXT,"
                    + "$COLUMN_PRODUCT_IMAGE_URI TEXT)")
            db.execSQL(createProductTableQuery)
        } else {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
            onCreate(db)
        }
    }
}