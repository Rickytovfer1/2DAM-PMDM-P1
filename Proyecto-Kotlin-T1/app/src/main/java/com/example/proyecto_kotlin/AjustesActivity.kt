package com.example.proyecto_kotlin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.proyecto_kotlin.repositorio.UsuarioRepositorio

class AjustesActivity : AppCompatActivity() {

    private lateinit var repositorioUsuario: UsuarioRepositorio
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        repositorioUsuario = UsuarioRepositorio(this)
        sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)

        val idUsuario = sharedPreferences.getLong("id_usuario", -1)
        if (idUsuario == -1L) {
            Toast.makeText(this, "Error: Usuario no encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val idUsuarioInt = idUsuario.toInt()

        val campoCorreo: EditText = findViewById(R.id.editTextEmail)
        val campoContrasena: EditText = findViewById(R.id.editTextContraseña)
        val botonGuardar: Button = findViewById(R.id.botonGuardarCambios)

        val botonEstadistica = findViewById<ImageButton>(R.id.botonEstadistica)
        val botonMenu = findViewById<ImageButton>(R.id.botonPrincipal)
        val botonVolver: ImageButton = findViewById(R.id.botonVolver)

        val switchNotificaciones = findViewById<Switch>(R.id.mostrarNotificaciones)

        val botonEliminarUsuario: Button = findViewById(R.id.eliminarUsuario)

        botonEliminarUsuario.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Confirmación")
                setMessage("¿Estás seguro de que deseas eliminar tu cuenta?")
                setPositiveButton("Sí") { _, _ ->
                    val idUsuario = sharedPreferences.getLong("id_usuario", -1)
                    AlertDialog.Builder(this@AjustesActivity).apply {
                        setTitle("Confirmación")
                        setMessage("Pon tu contraseña")
                        val campoContrasena = EditText(this@AjustesActivity).apply { inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD }
                        setView(campoContrasena)
                        setPositiveButton("Aceptar") { _, _ ->
                            val usuarioValido = repositorioUsuario.verificarUsuario(repositorioUsuario.obtenerEmail(idUsuarioInt).toString(), campoContrasena.text.toString())
                            if (usuarioValido != -1L) {
                                val eliminado = repositorioUsuario.eliminarUsuario(idUsuario)
                                if (eliminado) {
                                    Toast.makeText(this@AjustesActivity, "Usuario eliminado.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@AjustesActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@AjustesActivity, "Error al eliminar el usuario.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@AjustesActivity, "Error: Contraseña incorrecta.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        setNegativeButton("Cancelar", null)
                    }.show()
                }
                setNegativeButton("No", null)
            }.show()

        }

        val correo = repositorioUsuario.obtenerEmail(idUsuarioInt)

        campoCorreo.setText(correo)

        var notificaciones = repositorioUsuario.obtenerNotificaciones(idUsuarioInt)

        if (notificaciones == 1) {
            switchNotificaciones.isChecked = true
        } else {
            switchNotificaciones.isChecked = false
        }
        switchNotificaciones.setOnCheckedChangeListener { _, marcado ->
            if (marcado) {
                notificaciones = 1
                repositorioUsuario.actualizarNotificaciones(idUsuarioInt, notificaciones)
            } else {
                notificaciones = 0
                repositorioUsuario.actualizarNotificaciones(idUsuarioInt, notificaciones)
            }
        }

        botonGuardar.setOnClickListener {
            val nuevoCorreo = campoCorreo.text.toString()
            val nuevaContrasena = campoContrasena.text.toString()

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo).matches()) {
                Toast.makeText(this, "Por favor, introduce un email válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!nuevaContrasena.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"))) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, incluyendo letras, números, mayúsculas y minúsculas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var cambiosRealizados = false

            if (nuevoCorreo.isNotEmpty()) {
                cambiosRealizados = repositorioUsuario.actualizarEmail(idUsuarioInt, nuevoCorreo)
                if (cambiosRealizados) {
                    Toast.makeText(this, "Correo actualizado.", Toast.LENGTH_SHORT).show()
                }
            }

            if (nuevaContrasena.isNotEmpty()) {
                cambiosRealizados = repositorioUsuario.actualizarContrasena(idUsuarioInt, nuevaContrasena)
                if (cambiosRealizados) {
                    Toast.makeText(this, "Contraseña actualizada.", Toast.LENGTH_SHORT).show()
                }
            }

            if (!cambiosRealizados) {
                Toast.makeText(this, "No se realizaron cambios.", Toast.LENGTH_SHORT).show()
            }
        }

        val layoutPrincipal: ConstraintLayout = findViewById(R.id.main)

        repositorioUsuario.aplicarColorFondo(idUsuarioInt, layoutPrincipal)

        val botonesColores = mapOf(
            R.id.botonBlanco to "azul",
            R.id.botonRosa to "rosa",
            R.id.botonRojo to "naranja",
            R.id.botonVerde to "verde",
        )

        botonesColores.forEach { (botonId, color) ->
            findViewById<Button>(botonId).setOnClickListener {
                val actualizado = repositorioUsuario.actualizarColorApp(idUsuarioInt, color)
                if (actualizado) {
                    Toast.makeText(this, "Color actualizado a $color.", Toast.LENGTH_SHORT).show()
                    repositorioUsuario.aplicarColorFondo(idUsuarioInt, layoutPrincipal)
                    setResult(RESULT_OK)
                } else {
                    Toast.makeText(this, "Error al actualizar el color.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        findViewById<ImageButton>(R.id.botonNavidad).setOnClickListener {
            val actualizado = repositorioUsuario.actualizarColorApp(idUsuarioInt, "navidad")
            if (actualizado) {
                Toast.makeText(this, "Color actualizado a tema navideño.", Toast.LENGTH_SHORT).show()
                repositorioUsuario.aplicarColorFondo(idUsuarioInt, layoutPrincipal)
                setResult(RESULT_OK)
            } else {
                Toast.makeText(this, "Error al actualizar el color.", Toast.LENGTH_SHORT).show()
            }
        }
        botonMenu.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }

        botonEstadistica.setOnClickListener {
            val intent = Intent(this, ContadorActivity::class.java)
            startActivity(intent)
        }

        botonVolver.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }
}