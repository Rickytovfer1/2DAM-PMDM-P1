package com.example.proyecto_kotlin.BBDD

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Sql(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                contrasena TEXT NOT NULL,
                notificaciones INTEGER,
                color_app TEXT DEFAULT 'azul' CHECK (color_app IN ('blanco', 'rosa', 'naranja', 'verde', 'azul'))
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE notas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER,
                titulo TEXT NOT NULL,
                texto TEXT NOT NULL,
                estado TEXT CHECK (estado IN ('bien', 'regular', 'mal')),
                fecha_creacion DATE,
                FOREIGN KEY (id_usuario) REFERENCES usuario (id) ON DELETE CASCADE
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE eventos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_notas INTEGER,
                FOREIGN KEY (id_notas) REFERENCES notas (id) ON DELETE SET NULL
            );
            """.trimIndent()
        )
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys = ON;")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS eventos")
        db.execSQL("DROP TABLE IF EXISTS notas")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        onCreate(db)
    }

    fun updateEmail(userId: Int, newEmail: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("email", newEmail)
        }
        val result = db.update("usuario", contentValues, "id = ?", arrayOf(userId.toString()))
        return result > 0
    }

    fun updatePassword(userId: Int, newPassword: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("contrasena", newPassword)
        }
        val result = db.update("usuario", contentValues, "id = ?", arrayOf(userId.toString()))
        return result > 0
    }

    fun updateThemeColor(userId: Int, newColor: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("color_app", newColor)
        }
        val result = db.update("usuario", contentValues, "id = ?", arrayOf(userId.toString()))
        return result > 0
    }

    fun updateNotificaciones(userId: Int, notificaciones: Int): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("notificaciones", notificaciones)
        }
        val result = db.update("usuario", contentValues, "id = ?", arrayOf(userId.toString()))
        return result > 0
    }

    companion object {
        private const val DATABASE_NAME = "hilo.db"
        private const val DATABASE_VERSION = 1
    }
}