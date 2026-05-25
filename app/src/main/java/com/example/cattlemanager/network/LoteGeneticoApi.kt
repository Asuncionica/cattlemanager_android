package com.example.cattlemanager.network

import com.example.cattlemanager.model.LoteGeneticoResponse
import retrofit2.Response // IMPORTANTE: Asegúrate de que se importe esta clase
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoteGeneticoApi {

    @GET("api/lotes-geneticos")
    suspend fun getLotesGeneticos(): List<LoteGeneticoResponse>

    @POST("api/lotes-geneticos")
    suspend fun crearLoteGenetico(@Body lote: LoteGeneticoResponse): LoteGeneticoResponse

    @DELETE("api/lotes-geneticos/{id}")
    suspend fun eliminarLoteGenetico(@Path("id") id: Long): Response<Unit>
}