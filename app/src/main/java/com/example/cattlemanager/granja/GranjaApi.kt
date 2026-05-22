package com.example.cattlemanager.granja
import com.example.cattlemanager.model.Granja
import com.example.cattlemanager.model.GranjaRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
interface GranjaApi {
    @GET("granjas")
    suspend fun obtenerGranjas(): List<Granja>
    @GET("granjas/{id}")
    suspend fun obtenerGranja(@Path("id") id: Long): Granja
    @POST("granjas")
    suspend fun crearGranja(@Body granja: GranjaRequest): Granja
    @PUT("granjas/{id}")
    suspend fun actualizarGranja(
        @Path("id") id: Long,
        @Body granja: GranjaRequest
    ): Granja
}