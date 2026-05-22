package com.example.cattlemanager.model

// Cuerpo de la petición POST para crear un evento reproductivo con el animal referenciado por id
data class EventoReproductivoRequest(
    val tipo: String,
    val descripcion: String,
    val fecha: String,
    val animal: AnimalIdRequest
)
