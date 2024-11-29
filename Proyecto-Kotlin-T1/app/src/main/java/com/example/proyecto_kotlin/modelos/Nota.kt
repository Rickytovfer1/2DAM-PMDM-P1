package com.example.proyecto_kotlin.modelos

import java.time.LocalDate

data class Nota(
    var id: Long ?= null,
    var titulo: String,
    var texto: String,
    var estado: String,
    var fechaCreacion: LocalDate,
    var idUsuario: Long
)