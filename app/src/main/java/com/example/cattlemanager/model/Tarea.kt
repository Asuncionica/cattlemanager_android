package com.example.cattlemanager.model

data class Tarea(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val fechaVencimiento: String,
    val completada: Boolean,
    val granja: Granja?,
    val peon: Usuario?
)
