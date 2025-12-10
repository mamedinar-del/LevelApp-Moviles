package com.example.levelapp.data.repository

import android.content.Context
import com.example.levelapp.data.network.RetrofitClient
import com.example.levelapp.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(context: Context) {

    private val api = RetrofitClient.backendService

    suspend fun registrarUsuario(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.registerUser(user)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun iniciarSesion(email: String, contrasena: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val loginRequest = User(email = email, contrasena = contrasena, nombre = "", apellido = "", rut = "")
            val response = api.loginUser(loginRequest)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllUsersFromBackend()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun actualizarFotoPerfil(userId: Long, imagenUri: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userUpdate = User(
                id = userId,
                imagenPerfilUri = imagenUri,
                email = "", contrasena = "", nombre = "", apellido = "", rut = ""
            )

            val response = api.updateUserInBackend(userId, userUpdate)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar foto: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}