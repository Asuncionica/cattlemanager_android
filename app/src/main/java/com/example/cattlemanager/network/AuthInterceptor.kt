package com.example.cattlemanager.network

import okhttp3.Interceptor
import okhttp3.Response

// Interceptor que añade automáticamente el token de autenticación
// a todas las peticiones HTTP realizadas con Retrofit
class AuthInterceptor(private val token: String) : Interceptor {

    // Método que intercepta cada petición antes de enviarla
    override fun intercept(chain: Interceptor.Chain): Response {

        // Crea un builder a partir de la petición original
        val requestBuilder = chain.request().newBuilder()

        // Si hay token, lo añade como cabecera Authorization
        if (token.isNotBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // Continúa la petición con la cabecera añadida
        return chain.proceed(requestBuilder.build())
    }
}