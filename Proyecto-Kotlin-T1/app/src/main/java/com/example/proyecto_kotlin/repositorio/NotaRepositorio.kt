package com.example.proyecto_kotlin.repositorio

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyecto_kotlin.BBDD.Sql
import com.example.proyecto_kotlin.modelos.Nota
import java.time.LocalDate

class NotaRepositorio(context: Context) {

    private val dbsql = Sql(context)

    fun insertarNota(nota: Nota): Long {
        val db: SQLiteDatabase = dbsql.writableDatabase

        val valores = ContentValues().apply {
            put("titulo", nota.titulo)
            put("texto", nota.texto)
            put("estado", nota.estado)
            put("fecha_creacion", nota.fechaCreacion.toString())
            put("id_usuario", nota.idUsuario)

        }

        val id = db.insert("notas", null, valores)
        db.close()
        return id
    }
}
