package com.example.proyecto_kotlin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto_kotlin.BBDD.Sql
import com.example.proyecto_kotlin.utiles.ColorUtiles

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

        val botonAzul = findViewById<Button>(R.id.botonAzul)
        val botonAnaranjadoClaro = findViewById<Button>(R.id.botonAnaranjadoClaro)
        val botonAzulClaro = findViewById<Button>(R.id.botonAzulClaro)
        val botonPlomoClaro = findViewById<Button>(R.id.botonPlomoClaro)
        val botonNegroClaro = findViewById<Button>(R.id.botonNegroClaro)
        val botonEstadistica = findViewById<ImageButton>(R.id.botonEstadistica)
        val botonMenu = findViewById<ImageButton>(R.id.botonPrincipal)
        val switchNotificaciones = findViewById<Switch>(R.id.switchNotificaciones)

        val layout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val passwordField = findViewById<EditText>(R.id.editTextContrasena)

/*        val colorGuardado = ColorUtiles().obtenerColorGuardado(this)
        layout.setBackgroundColor(colorGuardado)*/

        botonAzul.setOnClickListener {
            actualizarColor(Color.parseColor("#3A7CA5"), sharedPreferences, layout)
        }
        botonNegroClaro.setOnClickListener {
            actualizarColor(Color.parseColor("#4A4A4A"), sharedPreferences, layout)
        }
        botonAnaranjadoClaro.setOnClickListener {
            actualizarColor(Color.parseColor("#F4A261"), sharedPreferences, layout)
        }
        botonAzulClaro.setOnClickListener {
            actualizarColor(Color.parseColor("#A8DADC"), sharedPreferences, layout)
        }
        botonPlomoClaro.setOnClickListener {
            actualizarColor(Color.parseColor("#008F11"), sharedPreferences, layout)
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

        switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                habilitarNotificaciones()
            } else {
                deshabilitarNotificaciones()
            }
            actualizarNotificacionesEnBD(isChecked)
        }

        // Retrieve and set email and password
        val db = Sql(this)
        val user = db.getUserById(userId)
        if (user != null) {
            emailField.setText(user.email)
            passwordField.setText(user.contrasena)
            passwordField.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
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

    private fun habilitarNotificaciones() {
        // Lógica para habilitar todas las notificaciones
        Toast.makeText(this, "Notificaciones habilitadas", Toast.LENGTH_SHORT).show()
    }

    private fun deshabilitarNotificaciones() {
        // Lógica para deshabilitar todas las notificaciones
        Toast.makeText(this, "Notificaciones deshabilitadas", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarNotificacionesEnBD(habilitar: Boolean) {
        val db = Sql(this)
        val notificaciones = if (habilitar) 1 else 0
        if (db.updateNotificaciones(userId, notificaciones)) {
            Toast.makeText(this, "Configuración de notificaciones actualizada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al actualizar configuración de notificaciones", Toast.LENGTH_SHORT).show()
        }
    }
}