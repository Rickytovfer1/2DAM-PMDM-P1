package com.example.proyecto_kotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_kotlin.repositorio.UsuarioRepositorio
import com.example.proyecto_kotlin.modelos.Usuario


// Esto no me gusta nada.
class RegistrarActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        val inputNombre = findViewById<EditText>(R.id.editTextNombre)
        val inputApellidos = findViewById<EditText>(R.id.editTextApellidos)
        val inputEmail = findViewById<EditText>(R.id.editTextEmail)
        val inputContrasena = findViewById<EditText>(R.id.editTextContrasena)
        val inputRepetirContrasena = findViewById<EditText>(R.id.repetirContraseña)
        val yaTengoCuenta = findViewById<TextView>(R.id.tengoCuenta)
        val btnGuardar = findViewById<Button>(R.id.botonRegistrar)

        val usuarioRepositorio = UsuarioRepositorio(this)

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

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Por favor, introduce un email válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != repetirContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!contrasena.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"))) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, incluyendo letras, números, mayúsculas y minúsculas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val opcionesNotificaciones = arrayOf(
                "Mostrar notificaciones",
                "No mostrar ninguna notificación"
            )

            var seleccion = 0

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Mostrar notificaciones")
            builder.setSingleChoiceItems(opcionesNotificaciones, seleccion) { _, which ->
                seleccion = which
            }
            builder.setPositiveButton("Aceptar") { _, _ ->
                val notificacionesHabilitadas = seleccion == 0
                guardarUsuario(usuarioRepositorio, nombre, apellidos, email, contrasena, notificacionesHabilitadas)
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
        yaTengoCuenta.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun guardarUsuario(
        usuarioRepositorio: UsuarioRepositorio,
        nombre: String,
        apellidos: String,
        email: String,
        contrasena: String,
        notificaciones: Boolean
    ) {
        val usuario = Usuario(
            nombre = nombre,
            apellidos = apellidos,
            email = email,
            contrasena = contrasena,
            notificaciones = notificaciones,
        )

        val id = usuarioRepositorio.insertarUsuario(usuario)
        if (id != -1L) {
            val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("id_usuario", id)
            editor.apply()

            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
        }
    }
}