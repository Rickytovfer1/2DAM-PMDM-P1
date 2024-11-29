package com.example.proyecto_kotlin.BBDD

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class sql(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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

        // Crear tabla eventos
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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS eventos")
        db.execSQL("DROP TABLE IF EXISTS notas")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "hilo.db"
        private const val DATABASE_VERSION = 1
    }
}
