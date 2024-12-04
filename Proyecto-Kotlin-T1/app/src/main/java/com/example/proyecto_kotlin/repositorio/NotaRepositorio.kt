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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun contarNotasEstado(idUsuario: Long): Map<String, Int> {
        val estadoContador = mutableMapOf<String, Int>()
        val db: SQLiteDatabase = dbsql.readableDatabase
        val mesActual = LocalDate.now().toString().substring(0, 7)

        val cursor = db.rawQuery(
            """
            SELECT estado, COUNT(*) as cantidad
            FROM notas
            WHERE strftime('%Y-%m', fecha_creacion) = ? AND id_usuario = ?
            GROUP BY estado
            """, arrayOf(mesActual, idUsuario.toString())
        )

        while (cursor.moveToNext()) {
            val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
            estadoContador[estado] = cantidad
        }

        cursor.close()
        db.close()

        return estadoContador
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

    fun eliminarNota(id: Long): Boolean {
        val db: SQLiteDatabase = dbsql.writableDatabase
        val filasAfectadas = db.delete("notas", "id = ?", arrayOf(id.toString()))
        db.close()
        return filasAfectadas > 0
    }
}
