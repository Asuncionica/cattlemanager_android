package com.example.cattlemanager.eventosreproductivos

import com.example.cattlemanager.model.EventoReproductivo
import com.example.cattlemanager.model.EventoReproductivoRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Define los endpoints REST del módulo de eventos reproductivos
interface EventoReproductivoApi {

    @GET("eventos-reproductivos")
    suspend fun obtenerEventos(): List<EventoReproductivo>

    @POST("eventos-reproductivos")
    suspend fun crearEvento(@Body evento: EventoReproductivoRequest): EventoReproductivo

    @DELETE("eventos-reproductivos/{id}")
    suspend fun borrarEvento(@Path("id") id: Long)
}
