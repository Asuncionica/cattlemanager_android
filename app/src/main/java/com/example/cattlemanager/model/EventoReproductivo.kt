package com.example.cattlemanager.model

// Representa un evento reproductivo recibido desde el backend (parto, inseminación, etc.)
data class EventoReproductivo(
    val id: Long,
    val tipo: String,
    val descripcion: String,
    val fecha: String,
    val animal: Animal?
)
