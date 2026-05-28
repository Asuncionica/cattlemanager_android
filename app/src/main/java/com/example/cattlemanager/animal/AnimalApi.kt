package com.example.cattlemanager.animal

import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AnimalRequest
import retrofit2.http.*

interface AnimalApi {

        // Recupera la lista completa de animales (Devuelve el modelo con relaciones)
        @GET("animales")
        suspend fun getAnimales(): List<Animal>

        // Obtiene un animal específico por su ID único
        @GET("animales/{id}")
        suspend fun obtenerAnimalPorId(@Path("id") id: Long): Animal

        // Registra un nuevo animal enviando el DTO simplificado al Backend
        @POST("animales")
        suspend fun crearAnimal(@Body animal: AnimalRequest): Animal

        // Actualiza los datos de un animal existente pasándole su ID y el DTO
        @PUT("animales/{id}")
        suspend fun actualizarAnimal(@Path("id") id: Long, @Body animal: AnimalRequest): Animal

        // Elimina un registro de la base de datos a través de su ID
        @DELETE("animales/{id}")
        suspend fun eliminarAnimal(@Path("id") id: Long)
}