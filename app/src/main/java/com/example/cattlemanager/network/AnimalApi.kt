package com.example.cattlemanager.network

import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AnimalRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Interfaz de Retrofit para gestionar las operaciones relacionadas con animales
interface AnimalApi {

    // GET: obtiene la lista completa de animales
    @GET("animales")
    suspend fun obtenerAnimales(): List<Animal>

    // GET: obtiene un animal concreto por su ID
    @GET("animales/{id}")
    suspend fun obtenerAnimalPorId(@Path("id") id: Long): Animal

    // POST: crea un nuevo animal en el backend
    @POST("animales")
    suspend fun crearAnimal(@Body animal: AnimalRequest): Animal

    // PUT: actualiza un animal existente
    @PUT("animales/{id}")
    suspend fun actualizarAnimal(
        @Path("id") id: Long,
        @Body animal: AnimalRequest
    ): Animal

    // DELETE: elimina un animal por su ID
    @DELETE("animales/{id}")
    suspend fun borrarAnimal(@Path("id") id: Long)
}