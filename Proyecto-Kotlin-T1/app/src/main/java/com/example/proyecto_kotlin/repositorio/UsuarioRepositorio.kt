package com.example.proyecto_kotlin.repositorio

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.proyecto_kotlin.BBDD.Sql
import com.example.proyecto_kotlin.R
import com.example.proyecto_kotlin.modelos.Usuario

class UsuarioRepositorio(context: Context) {

    private val dbsql = Sql(context)

    fun insertarUsuario(usuario: Usuario): Long {
        val db: SQLiteDatabase = dbsql.writableDatabase

        val valores = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("apellidos", usuario.apellidos)
            put("email", usuario.email)
            put("contrasena", usuario.contrasena)
            put("notificaciones", if (usuario.notificaciones) 1 else 0)
            put("color_app", usuario.colorApp)
        }

        val id = db.insert("usuario", null, valores)
        db.close()
        return id
    }

    fun verificarUsuario(email: String, contrasena: String): Long {
        val db: SQLiteDatabase = dbsql.readableDatabase
        var idUsuario: Long = -1

        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT id FROM usuario WHERE email = ? AND contrasena = ?",
                arrayOf(email, contrasena)
            )

            if (cursor.moveToFirst()) {
                idUsuario = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            }

            cursor.close()
        } catch (e: Exception) {
            println("Error")
        } finally {
            db.close()
        }

        return idUsuario
    }

    fun actualizarEmail(userId: Int, newEmail: String): Boolean {
        val db: SQLiteDatabase = dbsql.writableDatabase

        val valores = ContentValues().apply {
            put("email", newEmail)
        }

        val resultado = db.update(
            "usuario",
            valores,
            "id = ?",
            arrayOf(userId.toString())
        )

        db.close()
        return resultado > 0
    }

    fun actualizarContrasena(userId: Int, newPassword: String): Boolean {
        val db: SQLiteDatabase = dbsql.writableDatabase

        val valores = ContentValues().apply {
            put("contrasena", newPassword)
        }

        val resultado = db.update(
            "usuario",
            valores,
            "id = ?",
            arrayOf(userId.toString())
        )

        db.close()
        return resultado > 0
    }

    fun actualizarNotificaciones(userId: Int, notificaciones: Int): Boolean {
        val db: SQLiteDatabase = dbsql.writableDatabase

        val valores = ContentValues().apply {
            put("notificaciones", notificaciones)
        }

        val resultado = db.update(
            "usuario",
            valores,
            "id = ?",
            arrayOf(userId.toString())
        )

        db.close()
        return resultado > 0
    }

    fun obtenerEmail(userId: Int): String? {
        val db = dbsql.readableDatabase
        val cursor = db.query(
            "usuario",
            arrayOf("email"),
            "id = ?",
            arrayOf(userId.toString()),
            null, null, null
        )
        var email: String? = null
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
        }
        cursor.close()
        db.close()
        return email
    }

    fun obtenerNotificaciones(userId: Int): Int {
        val db = dbsql.readableDatabase
        val cursor = db.query(
            "usuario",
            arrayOf("notificaciones"),
            "id = ?",
            arrayOf(userId.toString()),
            null, null, null
        )
        var notificaciones = 0
        if (cursor.moveToFirst()) {
            notificaciones = cursor.getInt(cursor.getColumnIndexOrThrow("notificaciones"))
        }
        cursor.close()
        db.close()
        return notificaciones
    }

    fun obtenerColorApp(userId: Int): String? {
        val db = dbsql.readableDatabase
        val cursor = db.query(
            "usuario",
            arrayOf("color_app"),
            "id = ?",
            arrayOf(userId.toString()),
            null, null, null
        )
        var color: String? = null
        if (cursor.moveToFirst()) {
            color = cursor.getString(cursor.getColumnIndexOrThrow("color_app"))
        }
        cursor.close()
        db.close()
        return color
    }

    fun actualizarColorApp(userId: Int, color: String): Boolean {
        val db = dbsql.writableDatabase
        val valores = ContentValues().apply {
            put("color_app", color)
        }
        val resultado = db.update(
            "usuario",
            valores,
            "id = ?",
            arrayOf(userId.toString())
        )
        db.close()
        return resultado > 0
    }

    fun aplicarColorFondo(userId: Int, layout: ConstraintLayout) {
        val colorUsuario = obtenerColorApp(userId)
        val colorId = when (colorUsuario) {
            "azul" -> R.color.primario
            "rosa" -> R.color.rosa_claro
            "verde" -> R.color.verde_claro
            "naranja" -> R.color.naranja_claro

            else -> R.color.primario
        }
        layout.setBackgroundResource(colorId)
    }
}