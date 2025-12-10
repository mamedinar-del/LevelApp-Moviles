package com.example.levelapp.data.network

import com.example.levelapp.model.Product
import com.example.levelapp.model.RawgSearchResponse
import com.example.levelapp.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("games")
    suspend fun searchGames(
        @Query("key") apiKey: String,
        @Query("search") query: String,
        @Query("page_size") pageSize: Int = 5
    ): Response<RawgSearchResponse>

    @GET("api/products")
    suspend fun getProductsFromBackend(): Response<List<Product>>

    @POST("api/products")
    suspend fun createProductInBackend(@Body product: Product): Response<Product>

    @PUT("api/products/{id}")
    suspend fun updateProductInBackend(@Path("id") id: Long, @Body product: Product): Response<Product>

    @DELETE("api/products/{id}")
    suspend fun deleteProductInBackend(@Path("id") id: Long): Response<Void>

    @POST("api/auth/login")
    suspend fun loginUser(@Body user: User): Response<User>

    @POST("api/auth/register")
    suspend fun registerUser(@Body user: User): Response<User>

    @GET("api/auth/users")
    suspend fun getAllUsersFromBackend(): Response<List<User>>

    @PUT("api/auth/users/{id}")
    suspend fun updateUserInBackend(@Path("id") id: Long, @Body user: User): Response<User>
}