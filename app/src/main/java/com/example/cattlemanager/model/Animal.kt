package com.example.cattlemanager.model

data class Animal(
    val id: Long,
    val identificador: String,
    val raza: String,
    val sexo: String,
    val fechaNacimiento: String,
    val granja: Granja?
)
