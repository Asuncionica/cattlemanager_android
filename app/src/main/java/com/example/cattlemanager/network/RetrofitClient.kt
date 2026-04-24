package com.example.cattlemanager.network

import android.content.Context
import com.example.cattlemanager.alertas.AlertaVeterinariaApi
import com.example.cattlemanager.eventosproductivos.EventoProductivoApi
import com.example.cattlemanager.eventosreproductivos.EventoReproductivoApi
import com.example.cattlemanager.eventossanitarios.EventoSanitarioApi
import com.example.cattlemanager.tareas.TareaApi
import com.example.cattlemanager.granja.GranjaApi
import com.example.cattlemanager.usuarios.UsuarioApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.14:8085/"

    fun getAnimalApi(context: Context): AnimalApi {
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(AnimalApi::class.java)
    }
    fun getUsuarioApi(context: Context): UsuarioApi {
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(UsuarioApi::class.java)
    }
    fun getEventoProductivoApi(context: Context): EventoProductivoApi {
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(EventoProductivoApi::class.java)
    }
    fun getEventoReproductivoApi(context: Context): EventoReproductivoApi {
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(EventoReproductivoApi::class.java)
    }

    fun getEventoSanitarioApi(context: Context): EventoSanitarioApi {
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(EventoSanitarioApi::class.java)
    }

    fun getTareaApi(context: Context): TareaApi {
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(TareaApi::class.java)
    }

    fun getAlertaVeterinariaApi(context: Context): AlertaVeterinariaApi {
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""
        val client = OkHttpClient.Builder().addInterceptor(AuthInterceptor(token)).build()
        return Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(AlertaVeterinariaApi::class.java)
    }

    fun getGranjaApi(context: Context): GranjaApi {
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(GranjaApi::class.java)
    }
}