package com.example.cattlemanager.network

import com.example.cattlemanager.model.LoteGeneticoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoteGeneticoApi {

    @GET("api/lotes-geneticos")
    suspend fun getLotesGeneticos(): List<LoteGeneticoResponse>

    @POST("api/lotes-geneticos")
    suspend fun crearLoteGenetico(@Body lote: LoteGeneticoResponse): LoteGeneticoResponse
}

