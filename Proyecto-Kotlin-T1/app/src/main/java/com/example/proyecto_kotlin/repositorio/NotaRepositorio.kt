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

    fun modificarNota(idUsuario: Long, titulo: String, texto: String, fechaCreacion: LocalDate, estado: String): Boolean {
        val db: SQLiteDatabase = dbsql.writableDatabase

        val valores = ContentValues().apply {
            put("titulo", titulo)
            put("texto", texto)
            put("estado", estado)
        }

        val fila = db.update(
            "notas",
            valores,
            "id_usuario = ? AND fecha_creacion = ?",
            arrayOf(idUsuario.toString(), fechaCreacion.toString())
        )

        db.close()
        return fila > 0
    }

    fun obtenerNotaPorFecha(idUsuario: Long, fechaCreacion: LocalDate): Nota? {
        val db: SQLiteDatabase = dbsql.readableDatabase
        val cursor: Cursor = db.query(
            "notas",
            arrayOf("titulo", "texto", "estado", "fecha_creacion", "id_usuario"),
            "id_usuario = ? AND fecha_creacion = ?",
            arrayOf(idUsuario.toString(), fechaCreacion.toString()),
            null,
            null,
            null
        )

        var nota: Nota? = null
        if (cursor.moveToFirst()) {
            val titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"))
            val texto = cursor.getString(cursor.getColumnIndexOrThrow("texto"))
            val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            val fecha = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion")))

            nota = Nota(
                titulo = titulo,
                texto = texto,
                estado = estado,
                fechaCreacion = fecha,
                idUsuario = idUsuario
            )
        }

        cursor.close()
        db.close()
        return nota
    }


}
