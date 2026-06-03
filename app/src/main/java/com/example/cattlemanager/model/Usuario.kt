package com.example.cattlemanager.model

data class Usuario(
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: Rol
)
