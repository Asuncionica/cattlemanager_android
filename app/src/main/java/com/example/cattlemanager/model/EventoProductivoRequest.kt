package com.example.cattlemanager.model

data class EventoProductivoRequest(
    val tipo: String,
    val descripcion: String,
    val fecha: String,
    val animal: AnimalIdRequest
)
