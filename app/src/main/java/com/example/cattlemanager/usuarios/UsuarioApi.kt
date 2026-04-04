package com.example.cattlemanager.usuarios

import com.example.cattlemanager.model.Usuario
import com.example.cattlemanager.model.UsuarioRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApi {

    @GET("usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>

    @GET("usuarios/{id}")
    suspend fun obtenerUsuarioPorId(@Path("id") id: Long): Usuario

    @POST("usuarios")
    suspend fun crearUsuario(@Body usuario: UsuarioRequest): Usuario

    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: UsuarioRequest
    ): Usuario

    @DELETE("usuarios/{id}")
    suspend fun borrarUsuario(@Path("id") id: Long)
}