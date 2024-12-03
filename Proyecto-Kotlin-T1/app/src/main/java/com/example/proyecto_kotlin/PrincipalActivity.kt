package com.example.proyecto_kotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto_kotlin.repositorio.UsuarioRepositorio

class PrincipalActivity : AppCompatActivity() {

    private val usuarioRepositorio: UsuarioRepositorio by lazy { UsuarioRepositorio(this) }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.principalActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botonIrNotas = findViewById<Button>(R.id.botonNotas)
        val botonIrCalendario = findViewById<ImageButton>(R.id.botonCalendario)
        val botonIrAjuste = findViewById<ImageButton>(R.id.botonAjustes)
        val botonCerrarSesion = findViewById<ImageButton>(R.id.botonCerrarSesion)

        val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getLong("id_usuario", -1L)

        if (idUsuario != -1L) {
            val layout = findViewById<ConstraintLayout>(R.id.principalActivity)
            layout?.let {
                usuarioRepositorio.aplicarColorFondo(idUsuario.toInt(), it)
            }
        }


        botonIrNotas.setOnClickListener {
            val intent = Intent(this, NotasActivity::class.java)
            startActivity(intent)
        }

        botonIrCalendario.setOnClickListener {
            val intent = Intent(this, CalendarioActivity::class.java)
            startActivity(intent)
        }

        botonIrAjuste.setOnClickListener {
            val intent = Intent(this, AjustesActivity::class.java)
            startActivity(intent)
        }

        botonCerrarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}