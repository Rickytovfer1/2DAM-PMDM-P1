package com.example.proyecto_kotlin.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.proyecto_kotlin.BBDD.sql
import com.example.proyecto_kotlin.modelos.usuario

class UsuarioRepository(context: Context) {

    private val dbHelper = sql(context)

    fun insertarUsuario(usuario: usuario): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase

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
}
