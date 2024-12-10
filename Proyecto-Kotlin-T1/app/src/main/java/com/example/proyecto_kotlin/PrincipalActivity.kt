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
    private val REQUEST_CODE_AJUSTES = 1

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
            startActivityForResult(intent, REQUEST_CODE_AJUSTES)
        }

        botonCerrarSesion.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("id_usuario")
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_AJUSTES && resultCode == RESULT_OK) {
            val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
            val idUsuario = sharedPreferences.getLong("id_usuario", -1L)
            if (idUsuario != -1L) {
                val layout = findViewById<ConstraintLayout>(R.id.principalActivity)
                layout?.let {
                    usuarioRepositorio.aplicarColorFondo(idUsuario.toInt(), it)
                }
            }
        }
    }
}