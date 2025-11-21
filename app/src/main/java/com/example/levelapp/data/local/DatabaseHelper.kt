package com.example.levelapp.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.levelapp.R

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val context = context

    companion object {
        private const val DATABASE_NAME = "levelup.db"
        private const val DATABASE_VERSION = 7

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
        db.execSQL("CREATE TABLE $TABLE_PRODUCTS ($COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_PRODUCT_NAME TEXT, $COLUMN_PRODUCT_DESC TEXT, $COLUMN_PRODUCT_STOCK INTEGER, $COLUMN_PRODUCT_PRICE REAL, $COLUMN_PRODUCT_CATEGORY TEXT, $COLUMN_PRODUCT_IMAGE_URI TEXT)")
        db.execSQL("CREATE TABLE $TABLE_CART ($COLUMN_CART_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_CART_PRODUCT_ID INTEGER UNIQUE, $COLUMN_CART_PRODUCT_NAME TEXT, $COLUMN_CART_PRODUCT_PRICE REAL, $COLUMN_CART_PRODUCT_IMAGE_URI TEXT, $COLUMN_CART_QUANTITY INTEGER)")

        insertarProductosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        onCreate(db)
    }

    private fun insertarProductosIniciales(db: SQLiteDatabase) {
        fun getUri(resourceId: Int): String {
            return "android.resource://${context.packageName}/$resourceId"
        }


        db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_DESC, $COLUMN_PRODUCT_STOCK, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_CATEGORY, $COLUMN_PRODUCT_IMAGE_URI) " +
                "VALUES ('Consola PS5', 'Edición estándar con lector de disco', 15, 549990, 'Consolas', '${getUri(R.drawable.ps5)}')")

        db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_DESC, $COLUMN_PRODUCT_STOCK, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_CATEGORY, $COLUMN_PRODUCT_IMAGE_URI) " +
                "VALUES ('Control DualSense', 'Control inalámbrico color blanco', 50, 64990, 'Accesorios', '${getUri(R.drawable.control)}')")

        db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_DESC, $COLUMN_PRODUCT_STOCK, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_CATEGORY, $COLUMN_PRODUCT_IMAGE_URI) " +
                "VALUES ('Elden Ring', 'Juego del año GOTY', 30, 45990, 'Videojuegos', '${getUri(R.drawable.juego)}')")

        db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_DESC, $COLUMN_PRODUCT_STOCK, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_CATEGORY, $COLUMN_PRODUCT_IMAGE_URI) " +
                "VALUES ('Audifono Inalambrico', 'Audífonos con cancelación de ruido', 20, 29990, 'Periféricos', '${getUri(R.drawable.audifonos)}')")
    }
}