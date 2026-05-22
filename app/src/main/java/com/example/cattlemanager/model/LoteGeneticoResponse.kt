package com.example.cattlemanager.model

data class LoteGeneticoResponse(
    val id: Long? = null,
    val nombre: String,
    val variedad: String? = null,
    val descripcion: String?,
    val fechaCreacion: String?
)
