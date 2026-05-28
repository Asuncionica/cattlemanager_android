package com.example.cattlemanager.model

data class UsuarioRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: Rol
)