package com.example.proyecto_kotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto_kotlin.repositorio.UsuarioRepositorio

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val repositorio = UsuarioRepositorio(this)


        val botonIniciar = findViewById<Button>(R.id.botonIniciarSesionn)
        val registrarse = findViewById<TextView>(R.id.registrarse)

        val editTextEmail = findViewById<EditText>(R.id.editTextEmailRegistrado)
        val editTextContrasena = findViewById<EditText>(R.id.editTextContrasenaRegistrada)


        botonIniciar.setOnClickListener {
            val usuarioValido = repositorio.verificarUsuario(editTextEmail.text.toString(),
                editTextContrasena.text.toString())

            if (usuarioValido != -1L) {
                val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putLong("id_usuario", usuarioValido)
                editor.apply()

                val intent = Intent(this, PrincipalActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }

        }

        registrarse.setOnClickListener {
            val intent = Intent(this, RegistrarActivity::class.java)
            startActivity(intent)
        }
    }
}