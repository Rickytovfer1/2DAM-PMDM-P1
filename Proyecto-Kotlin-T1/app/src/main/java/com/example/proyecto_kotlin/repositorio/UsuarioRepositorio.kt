package com.example.proyecto_kotlin.repositorio

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.proyecto_kotlin.BBDD.Sql
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
}
