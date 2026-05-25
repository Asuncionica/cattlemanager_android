package com.example.cattlemanager.network

import com.example.cattlemanager.security.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")

        sessionManager.getToken()
            ?.takeIf { it.isNotBlank() }
            ?.let { token -> requestBuilder.header("Authorization", "Bearer $token") }

        val response = chain.proceed(requestBuilder.build())
        if (response.code() == 401) {
            sessionManager.clearSession()
        }
        return response
    }
}
