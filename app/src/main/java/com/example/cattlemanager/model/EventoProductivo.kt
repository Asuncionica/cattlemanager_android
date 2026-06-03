package com.example.cattlemanager.model

data class EventoProductivo(
    val id: Long,
    val tipo: String,
    val fecha: String,
    val descripcion: String,
    val animal: Animal?
)