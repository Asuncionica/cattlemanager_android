package com.example.cattlemanager.model

data class EventoSanitario(
    val id: Long,
    val tipo: String,
    val descripcion: String,
    val fecha: String,
    val animal: Animal?
)
