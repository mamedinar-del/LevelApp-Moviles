package com.example.levelapp.data.network

import com.example.levelapp.model.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BackendApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<Product>
}