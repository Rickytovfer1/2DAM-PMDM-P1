package com.example.proyecto_kotlin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto_kotlin.BBDD.Sql

class AjustesActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ajustes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userId = obtenerUsuarioLogueado()

        val sharedPreferences = getSharedPreferences("ColorPreferences", MODE_PRIVATE)

        val botonBlanco = findViewById<Button>(R.id.botonBlanco)
        val botonRosa = findViewById<Button>(R.id.botonRosa)
        val botonRojo = findViewById<Button>(R.id.botonRojo)
        val botonVerde = findViewById<Button>(R.id.botonVerde)
        val botonEstadistica = findViewById<ImageButton>(R.id.botonEstadistica)
        val botonMenu = findViewById<ImageButton>(R.id.botonPrincipal)

        val layout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val passwordField = findViewById<EditText>(R.id.editTextContrasena)


        val colorGuardado = sharedPreferences.getInt("color", Color.WHITE)
        layout.setBackgroundColor(colorGuardado)


        botonBlanco.setOnClickListener {
            actualizarColor(Color.WHITE, sharedPreferences, layout)
        }
        botonRosa.setOnClickListener {
            actualizarColor(Color.MAGENTA, sharedPreferences, layout)
        }
        botonRojo.setOnClickListener {
            actualizarColor(Color.RED, sharedPreferences, layout)
        }
        botonVerde.setOnClickListener {
            actualizarColor(Color.GREEN, sharedPreferences, layout)
        }

        botonMenu.setOnClickListener {
            startActivity(Intent(this, PrincipalActivity::class.java))
        }
        botonEstadistica.setOnClickListener {
            startActivity(Intent(this, ContadorMain::class.java))
        }

        findViewById<Button>(R.id.botonGuardar).setOnClickListener {
            guardarCambios(emailField, passwordField, sharedPreferences)
        }
    }

    private fun obtenerUsuarioLogueado(): Int {

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        return sharedPreferences.getInt("userId", -1) // -1 si no se encuentra el ID
    }

    private fun actualizarColor(color: Int, sharedPreferences: android.content.SharedPreferences, layout: androidx.constraintlayout.widget.ConstraintLayout) {
        layout.setBackgroundColor(color)
        sharedPreferences.edit().putInt("color", color).apply()
        Toast.makeText(this, "Color actualizado", Toast.LENGTH_SHORT).show()
    }

    private fun guardarCambios(
        emailField: EditText,
        passwordField: EditText,
        sharedPreferences: android.content.SharedPreferences
    ) {
        val email = emailField.text.toString().trim()
        val password = passwordField.text.toString().trim()
        var cambiosRealizados = false

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no logueado", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            val db = Sql(this)
            if (db.updateEmail(userId, email)) {
                cambiosRealizados = true
                Toast.makeText(this, "Correo actualizado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar correo", Toast.LENGTH_SHORT).show()
            }
        } else if (email.isNotEmpty()) {
            emailField.error = "Introduce un email válido"
        }

        if (password.isNotEmpty() && password.length >= 8) {
            val db = Sql(this)
            if (db.updatePassword(userId, password)) {
                cambiosRealizados = true
                Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show()
            }
        } else if (password.isNotEmpty()) {
            passwordField.error = "La contraseña debe tener al menos 8 caracteres"
        }

        if (!cambiosRealizados) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
        }
    }
}