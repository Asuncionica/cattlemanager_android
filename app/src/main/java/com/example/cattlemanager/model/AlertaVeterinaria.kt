package com.example.cattlemanager.model

data class AlertaVeterinaria(
    val id: Long,
    val descripcion: String,
    val fecha: String,
    val atendida: Boolean,
    val animal: Animal?,
    val creadoPor: Usuario?
)
