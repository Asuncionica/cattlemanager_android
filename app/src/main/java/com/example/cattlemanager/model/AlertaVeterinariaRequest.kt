package com.example.cattlemanager.model

data class AlertaVeterinariaRequest(
    val descripcion: String,
    val animal: AnimalIdRequest,
    val creadoPor: UsuarioIdRequest
)
