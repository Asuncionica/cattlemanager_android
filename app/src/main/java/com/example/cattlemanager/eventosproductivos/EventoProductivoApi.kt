package com.example.cattlemanager.eventosproductivos

import com.example.cattlemanager.model.EventoProductivo
import com.example.cattlemanager.model.EventoProductivoRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventoProductivoApi {

    @GET("eventos-productivos")
    suspend fun obtenerEventos(): List<EventoProductivo>

    @POST("eventos-productivos")
    suspend fun crearEvento(@Body evento: EventoProductivoRequest): EventoProductivo

    @DELETE("eventos-productivos/{id}")
    suspend fun borrarEvento(@Path("id") id: Long)
}