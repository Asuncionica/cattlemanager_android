package com.example.cattlemanager.granja

import com.example.cattlemanager.model.Granja
import com.example.cattlemanager.model.GranjaRequest
import retrofit2.http.*

interface GranjaApi {

    @GET("granjas")
    suspend fun obtenerGranjas(): List<Granja>

    @GET("granjas/{id}")
    suspend fun obtenerGranja(@Path("id") id: Long): Granja

    @PUT("granjas/{id}")
    suspend fun actualizarGranja(
        @Path("id") id: Long,
        @Body granja: GranjaRequest
    ): Granja
}