package com.example.proyecto_kotlin.utiles

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.proyecto_kotlin.BBDD.Sql

class ColorUtiles {

    fun obtenerColorTema(context: Context, userId: Int): String {
        val db = Sql(context)
        val user = db.getUserById(userId)
        return user?.color_app ?: "#3A7CA5" // Color por defecto si no se encuentra
    }

    fun aplicarColorTema(context: Context, userId: Int, rootView: ConstraintLayout) {
        val color = obtenerColorTema(context, userId)
        try {
            rootView.setBackgroundColor(Color.parseColor(color))
        } catch (e: IllegalArgumentException) {
            Toast.makeText(context, "Color inválido, usando predeterminado", Toast.LENGTH_SHORT).show()
            rootView.setBackgroundColor(Color.WHITE)
        }
    }
}