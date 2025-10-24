package com.example.levelapp.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.levelapp.data.local.DatabaseHelper
import com.example.levelapp.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    suspend fun registrarUsuario(email: String, contrasena: String): Result<Unit> = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase

        val cursor = db.query(DatabaseHelper.TABLE_USERS, arrayOf(DatabaseHelper.COLUMN_ID), "${DatabaseHelper.COLUMN_EMAIL} = ?", arrayOf(email), null, null, null)
        if (cursor.moveToFirst()) {
            cursor.close()
            return@withContext Result.failure(Exception("El correo electrónico ya está registrado."))
        }
        cursor.close()

        // Si no existe, lo insertamos
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_EMAIL, email)
            put(DatabaseHelper.COLUMN_PASSWORD, contrasena)
        }

        val newRowId = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        if (newRowId == -1L) {
            Result.failure(Exception("Error al registrar el usuario."))
        } else {
            Result.success(Unit)
        }
    }

    suspend fun iniciarSesion(email: String, contrasena: String): Result<User> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_EMAIL, DatabaseHelper.COLUMN_PASSWORD),
            "${DatabaseHelper.COLUMN_EMAIL} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?",
            arrayOf(email, contrasena),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)),
                contrasena = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD))
            )
            cursor.close()
            Result.success(user)
        } else {
            cursor.close()
            Result.failure(Exception("Correo o contraseña incorrectos."))
        }
    }
}