package com.example.cattlemanager.model

data class AnimalRequest(
    val identificador: String,
    val raza: String,
    val sexo: String,
    val fechaNacimiento: String,
    val granja: GranjaIdRequest
)