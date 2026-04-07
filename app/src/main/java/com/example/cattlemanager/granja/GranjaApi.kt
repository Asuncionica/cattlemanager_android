package com.example.cattlemanager.granja

import com.example.cattlemanager.model.Granja
import com.example.cattlemanager.model.GranjaRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GranjaApi {

    @GET("granjas/usuario/{usuarioId}")
    suspend fun obtenerGranjasPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): List<Granja>

    @POST("granjas/usuario/{usuarioId}")
    suspend fun crearGranja(
        @Path("usuarioId") usuarioId: Long,
        @Body granja: GranjaRequest
    ): Granja

    @PUT("granjas/usuario/{usuarioId}/{id}")
    suspend fun actualizarGranja(
        @Path("usuarioId") usuarioId: Long,
        @Path("id") id: Long,
        @Body granja: GranjaRequest
    ): Granja

    @DELETE("granjas/usuario/{usuarioId}/{id}")
    suspend fun eliminarGranja(
        @Path("usuarioId") usuarioId: Long,
        @Path("id") id: Long
    )
}