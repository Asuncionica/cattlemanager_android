package com.example.cattlemanager.network

import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AnimalRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AnimalApi {

    @GET("animales")
    suspend fun obtenerAnimales(): List<Animal>

    @GET("animales/{id}")
    suspend fun obtenerAnimalPorId(@Path("id") id: Long): Animal
    @POST("animales")
    suspend fun crearAnimal(@Body animal: AnimalRequest): Animal

    @PUT("animales/{id}")
    suspend fun actualizarAnimal(
        @Path("id") id: Long,
        @Body animal: AnimalRequest
    ): Animal

    @DELETE("animales/{id}")
    suspend fun borrarAnimal(@Path("id") id: Long)
}