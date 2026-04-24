package com.example.cattlemanager.model

data class TareaRequest(
    val titulo: String,
    val descripcion: String,
    val fechaVencimiento: String,
    val completada: Boolean,
    val granja: GranjaIdRequest,
    val peon: UsuarioIdRequest?
)
