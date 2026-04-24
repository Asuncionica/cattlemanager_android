package com.example.cattlemanager.alertas

import com.example.cattlemanager.model.AlertaVeterinaria
import com.example.cattlemanager.model.AlertaVeterinariaRequest
import retrofit2.http.*

interface AlertaVeterinariaApi {

    // null = todas, false = pendientes, true = atendidas
    @GET("alertas-veterinaria")
    suspend fun obtenerAlertas(@Query("atendida") atendida: Boolean?): List<AlertaVeterinaria>

    @POST("alertas-veterinaria")
    suspend fun crearAlerta(@Body alerta: AlertaVeterinariaRequest): AlertaVeterinaria

    // El vet marca la alerta como atendida (queda en histórico)
    @PUT("alertas-veterinaria/{id}/atender")
    suspend fun marcarAtendida(@Path("id") id: Long): AlertaVeterinaria

    @DELETE("alertas-veterinaria/{id}")
    suspend fun eliminarAlerta(@Path("id") id: Long)
}
