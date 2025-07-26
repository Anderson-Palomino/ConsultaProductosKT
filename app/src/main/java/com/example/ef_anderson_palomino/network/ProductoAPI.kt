package com.example.ef_anderson_palomino.network

import com.example.ef_anderson_palomino.model.Producto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductoAPI{
    @GET ("products/{id}")
    fun getProducto(@Path("id") id: Int): Call<Producto>
}