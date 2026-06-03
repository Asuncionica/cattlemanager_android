package com.example.cattlemanager.model

// 1. Importamos el modelo de LoteGenetico (asegúrate de que la ruta sea correcta)

data class Animal(
    val id: Long,
    val identificador: String,
    val raza: String,
    val sexo: String,
    val fechaNacimiento: String,
    val granja: Granja?,

    // 2. Añadimos el nuevo campo para el lote genético (Nullable)
    val loteGenetico: LoteGeneticoResponse?
)