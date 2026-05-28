package com.example.cattlemanager.eventossanitarios

import com.example.cattlemanager.model.EventoSanitario
import com.example.cattlemanager.model.EventoSanitarioRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Define los endpoints REST del módulo de eventos sanitarios
interface EventoSanitarioApi {

    @GET("eventos-sanitarios")
    suspend fun obtenerEventos(): List<EventoSanitario>

    @POST("eventos-sanitarios")
    suspend fun crearEvento(@Body evento: EventoSanitarioRequest): EventoSanitario

    @DELETE("eventos-sanitarios/{id}")
    suspend fun borrarEvento(@Path("id") id: Long)
}
