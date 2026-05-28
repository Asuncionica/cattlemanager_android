package com.example.cattlemanager.model

data class GranjaRequest(
    val id: Long,
    val nombre: String,
    val ubicacion: String,
    val telefono: String,
    val latitude: Double?,
    val longitude: Double?
)
