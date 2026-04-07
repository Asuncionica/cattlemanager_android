package com.example.cattlemanager.network

import android.content.Context
import com.example.cattlemanager.eventosproductivos.EventoProductivoApi
import com.example.cattlemanager.granja.GranjaApi
import com.example.cattlemanager.usuarios.UsuarioApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto singleton que gestiona la creación de Retrofit y las APIs
object RetrofitClient {

    // URL base del backend
    private const val BASE_URL = "http://192.168.1.133:8085/"

    // Función privada que construye la instancia de Retrofit
    private fun buildRetrofit(context: Context): Retrofit {

        // Obtiene las preferencias compartidas donde se guarda el token
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)

        // Recupera el token de autenticación
        val token = prefs.getString("TOKEN", "") ?: ""

        // Crea el cliente HTTP
        val clientBuilder = OkHttpClient.Builder()

        // Si hay token, se añade un interceptor para incluirlo en las peticiones
        if (token.isNotBlank()) {
            clientBuilder.addInterceptor(AuthInterceptor(token))
        }

        // Construye Retrofit con:
        // - URL base
        // - cliente HTTP configurado
        // - convertidor JSON (Gson)
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Devuelve una instancia de AnimalApi
    fun getAnimalApi(context: Context): AnimalApi {
        return buildRetrofit(context).create(AnimalApi::class.java)
    }

    // Devuelve una instancia de UsuarioApi
    fun getUsuarioApi(context: Context): UsuarioApi {
        return buildRetrofit(context).create(UsuarioApi::class.java)
    }

    // Devuelve una instancia de EventoProductivoApi
    fun getEventoProductivoApi(context: Context): EventoProductivoApi {
        return buildRetrofit(context).create(EventoProductivoApi::class.java)
    }

    // Devuelve una instancia de GranjaApi
    fun getGranjaApi(context: Context): GranjaApi {
        return buildRetrofit(context).create(GranjaApi::class.java)
    }
}