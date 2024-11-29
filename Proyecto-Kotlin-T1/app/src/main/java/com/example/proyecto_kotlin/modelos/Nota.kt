package com.example.proyecto_kotlin.modelos

import java.time.LocalDate

data class Nota (
    var id: Int? = null,
    var titulo: String,
    var texto: String,
    var estado: String,
    var fechaCreacion : LocalDate,
    var idUsuario: Usuario
)