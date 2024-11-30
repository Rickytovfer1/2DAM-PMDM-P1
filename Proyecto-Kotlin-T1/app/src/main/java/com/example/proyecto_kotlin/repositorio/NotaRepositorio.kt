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

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerNotaFecha(idUsuario: Long, fechaCreacion: LocalDate): Nota? {
        val db: SQLiteDatabase = dbsql.readableDatabase

        val fechaCreacionStr = fechaCreacion.toString()

        val cursor: Cursor = db.query(
            "notas",
            arrayOf("id", "titulo", "texto", "estado", "fecha_creacion", "id_usuario"),
            "id_usuario = ? AND fecha_creacion = ?",
            arrayOf(idUsuario.toString(), fechaCreacionStr),
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex("id")
            val tituloIndex = cursor.getColumnIndex("titulo")
            val textoIndex = cursor.getColumnIndex("texto")
            val estadoIndex = cursor.getColumnIndex("estado")
            val fechaCreacionIndex = cursor.getColumnIndex("fecha_creacion")

            if (idIndex == -1 || tituloIndex == -1 || textoIndex == -1 || estadoIndex == -1 || fechaCreacionIndex == -1) {
                cursor.close()
                db.close()
                return null
            }

            val id = cursor.getLong(idIndex)
            val titulo = cursor.getString(tituloIndex)
            val texto = cursor.getString(textoIndex)
            val estado = cursor.getString(estadoIndex)
            val fecha = LocalDate.parse(cursor.getString(fechaCreacionIndex))

            cursor.close()
            db.close()

            return Nota(id, titulo, texto, estado, fecha, idUsuario)
        }

        cursor.close()
        db.close()
        return null
    }

    fun obtenerNotasEstado(estado: String, idUsuario: Long): Int {
        val db: SQLiteDatabase = dbsql.readableDatabase

        val cursor: Cursor = db.query(
            "notas",
            arrayOf("COUNT(*)"),
            "estado = ? AND id_usuario = ?",
            arrayOf(estado, idUsuario.toString()),
            null,
            null,
            null
        )

        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return count
    }
}
