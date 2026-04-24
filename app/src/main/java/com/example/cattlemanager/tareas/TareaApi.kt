package com.example.cattlemanager.tareas

import com.example.cattlemanager.model.Tarea
import com.example.cattlemanager.model.TareaPageResponse
import com.example.cattlemanager.model.TareaRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TareaApi {

    @GET("tareas")
    suspend fun obtenerTareas(): TareaPageResponse

    @GET("tareas/peon/{peonId}")
    suspend fun obtenerTareasPorPeon(
        @Path("peonId") peonId: Long,
        @Query("completada") completada: Boolean? = null
    ): TareaPageResponse

    @POST("tareas")
    suspend fun crearTarea(@Body tarea: TareaRequest): Tarea

    @PUT("tareas/{id}")
    suspend fun actualizarTarea(@Path("id") id: Long, @Body tarea: TareaRequest): Tarea

    @DELETE("tareas/{id}")
    suspend fun eliminarTarea(@Path("id") id: Long)
}
