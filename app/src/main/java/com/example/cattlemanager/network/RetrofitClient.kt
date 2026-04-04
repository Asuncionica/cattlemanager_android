package com.example.cattlemanager.network

import android.content.Context
import com.example.cattlemanager.eventosproductivos.EventoProductivoApi
import com.example.cattlemanager.granja.GranjaApi
import com.example.cattlemanager.usuarios.UsuarioApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.133:8085/"

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