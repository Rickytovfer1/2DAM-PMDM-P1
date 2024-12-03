package com.example.proyecto_kotlin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.proyecto_kotlin.repositorio.NotaRepositorio
import com.example.proyecto_kotlin.repositorio.UsuarioRepositorio
import java.time.LocalDate

class ContadorActivity : AppCompatActivity() {

    private val usuarioRepositorio: UsuarioRepositorio by lazy { UsuarioRepositorio(this) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contador)

        val notaRepositorio = NotaRepositorio(this)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val botonMenu = findViewById<ImageButton>(R.id.botonPrincipal)

        val mesActual = LocalDate.now().toString().substring(0, 7)
        val diasMes = when(mesActual.substring(5, 7)){
            "01", "03", "05", "07", "08", "10", "12" -> 31
            "04", "06", "09", "11" -> 30
            "02" -> 28
            else -> 0
        }
        val progreso = (100 * LocalDate.now().dayOfMonth) / diasMes

        progressBar.progress = progreso

        val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getLong("id_usuario", -1L)

        val estadoContador = notaRepositorio.contarNotasEstado(idUsuario)

        if (idUsuario != -1L) {
            val layout = findViewById<ConstraintLayout>(R.id.contadorActivity)
            layout?.let {
                usuarioRepositorio.aplicarColorFondo(idUsuario.toInt(), it)
            }
            actualizarContadores(estadoContador)

        }

        botonMenu.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }
    }

    private fun actualizarContadores(estadoContador: Map<String, Int>) {
        val txtContadorMal = findViewById<TextView>(R.id.txtContadorMal)
        val txtContadorRegular = findViewById<TextView>(R.id.txtContadorRegular)
        val txtContadorBien = findViewById<TextView>(R.id.txtContadorBien)

        txtContadorMal.text = estadoContador["mal"]?.toString() ?: "0"
        txtContadorRegular.text = estadoContador["regular"]?.toString() ?: "0"
        txtContadorBien.text = estadoContador["bien"]?.toString() ?: "0"
    }
}
