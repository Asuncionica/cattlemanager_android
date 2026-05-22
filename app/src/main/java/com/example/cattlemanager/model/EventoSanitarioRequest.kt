package com.example.cattlemanager.model

data class EventoSanitarioRequest(
    val tipo: String,
    val descripcion: String,
    val fecha: String,
    val animal: AnimalIdRequest
)
