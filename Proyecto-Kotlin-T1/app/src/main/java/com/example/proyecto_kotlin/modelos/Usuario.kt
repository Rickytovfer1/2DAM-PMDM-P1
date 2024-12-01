package com.example.proyecto_kotlin.modelos

data class Usuario(
    var id: Int? = null,
    var nombre: String,
    var apellidos: String,
    var email: String,
    var contrasena: String,
    var notificaciones: Boolean = true,
    var colorApp: String = "azul"
)
