package com.example.proyecto_kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_kotlin.database.UsuarioRepository
import com.example.proyecto_kotlin.modelos.usuario

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputNombre = findViewById<EditText>(R.id.editTextNombre)
        val inputApellidos = findViewById<EditText>(R.id.editTextApellidos)
        val inputEmail = findViewById<EditText>(R.id.editTextEmail)
        val inputContrasena = findViewById<EditText>(R.id.editTextContrasena)
        val inputRepetirContrasena = findViewById<EditText>(R.id.repetirContraseña)
        val btnGuardar = findViewById<Button>(R.id.botonRegistrar)

        if (btnGuardar == null) {
            Log.e("MainActivity", "Button 'botonGuardar' not found in layout")
            return
        }

        val usuarioRepository = UsuarioRepository(this)

        btnGuardar.setOnClickListener {
            val nombre = inputNombre.text.toString()
            val apellidos = inputApellidos.text.toString()
            val email = inputEmail.text.toString()
            val contrasena = inputContrasena.text.toString()
            val repetirContrasena = inputRepetirContrasena.text.toString()

            if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || contrasena.isEmpty() || repetirContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != repetirContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val opcionesNotificaciones = arrayOf(
                "Mostrar conversaciones predeterminadas y silenciosas",
                "No mostrar ninguna notificación"
            )

            var seleccion = 0

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Notificaciones en la pantalla de bloqueo")
            builder.setSingleChoiceItems(opcionesNotificaciones, seleccion) { _, which ->
                seleccion = which
            }
            builder.setPositiveButton("Aceptar") { _, _ ->
                val notificacionesHabilitadas = seleccion == 0
                guardarUsuario(usuarioRepository, nombre, apellidos, email, contrasena, notificacionesHabilitadas)
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    fun guardarUsuario(
        usuarioRepository: UsuarioRepository,
        nombre: String,
        apellidos: String,
        email: String,
        contrasena: String,
        notificaciones: Boolean
    ) {
        val usuario = usuario(
            nombre = nombre,
            apellidos = apellidos,
            email = email,
            contrasena = contrasena,
            notificaciones = notificaciones,
        )

        val id = usuarioRepository.insertarUsuario(usuario)
        if (id != -1L) {
            Toast.makeText(this, "Usuario guardado con éxito (ID: $id)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al guardar el usuario", Toast.LENGTH_SHORT).show()
        }
    }
}