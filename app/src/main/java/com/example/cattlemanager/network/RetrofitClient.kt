package com.example.cattlemanager.network

import android.content.Context
import com.example.cattlemanager.alertas.AlertaVeterinariaApi
import com.example.cattlemanager.eventosproductivos.EventoProductivoApi
import com.example.cattlemanager.eventosreproductivos.EventoReproductivoApi
import com.example.cattlemanager.eventossanitarios.EventoSanitarioApi
import com.example.cattlemanager.granja.GranjaApi
import com.example.cattlemanager.security.SessionManager
import com.example.cattlemanager.tareas.TareaApi
import com.example.cattlemanager.usuarios.UsuarioApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.18.37:8085/"

    private fun createHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(SessionManager(context)))
            .build()
    }

    private fun createRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAnimalApi(context: Context): AnimalApi =
        createRetrofit(context).create(AnimalApi::class.java)

    fun getUsuarioApi(context: Context): UsuarioApi =
        createRetrofit(context).create(UsuarioApi::class.java)

    fun getEventoProductivoApi(context: Context): EventoProductivoApi =
        createRetrofit(context).create(EventoProductivoApi::class.java)

    fun getEventoReproductivoApi(context: Context): EventoReproductivoApi =
        createRetrofit(context).create(EventoReproductivoApi::class.java)

    fun getEventoSanitarioApi(context: Context): EventoSanitarioApi =
        createRetrofit(context).create(EventoSanitarioApi::class.java)

    fun getTareaApi(context: Context): TareaApi =
        createRetrofit(context).create(TareaApi::class.java)

    fun getAlertaVeterinariaApi(context: Context): AlertaVeterinariaApi =
        createRetrofit(context).create(AlertaVeterinariaApi::class.java)

    fun getGranjaApi(context: Context): GranjaApi =
        createRetrofit(context).create(GranjaApi::class.java)

    fun getLoteGeneticoApi(context: Context): LoteGeneticoApi {
        return createRetrofit(context).create(LoteGeneticoApi::class.java)
    }
}
