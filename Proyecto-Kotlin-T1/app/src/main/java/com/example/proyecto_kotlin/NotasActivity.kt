package com.example.proyecto_kotlin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto_kotlin.modelos.Nota
import com.example.proyecto_kotlin.modelos.Usuario
import com.example.proyecto_kotlin.repositorio.NotaRepositorio
import com.example.proyecto_kotlin.repositorio.UsuarioRepositorio
import java.time.LocalDate

class NotasActivity : AppCompatActivity()
{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main))
        { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val editTextNota = findViewById<EditText>(R.id.editTextNota)


        val botonEstadistica = findViewById<ImageButton>(R.id.botonEstadistica)
        val botonMenu = findViewById<ImageButton>(R.id.botonPrincipal)
        val botonGuardar = findViewById<Button>(R.id.botonGuardar)

        val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getLong("id_usuario", -1L)

        botonGuardar.setOnClickListener {
            val textoNota = editTextNota.text.toString()
            val tituloNota = "Nota rápida"
            val estadoNota = "regular"
            val fechaCreacion = LocalDate.now()

            if (textoNota.isBlank())
            {
                Toast.makeText(this, "Por favor, escribe algo en la nota", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (idUsuario == -1L)
            {
                Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val notaRepositorio = NotaRepositorio(this)

            guardarNota(
                notaRepositorio,
                tituloNota,
                textoNota,
                estadoNota,
                fechaCreacion,
                idUsuario
            )
        }

        botonMenu.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }

        botonEstadistica.setOnClickListener {
            val intent = Intent(this, ContadorMain::class.java)
            startActivity(intent)
        }
    }
    fun guardarNota(
        notaRepositorio: NotaRepositorio,
        titulo: String,
        texto: String,
        estado: String,
        fechaCreacion: LocalDate,
        idUsuario: Long
    ) {
        val nota = Nota(
            titulo = titulo,
            texto = texto,
            estado = estado,
            fechaCreacion = fechaCreacion,
            idUsuario = idUsuario,
        )

        val idNota = notaRepositorio.insertarNota(nota)
        if (idNota != -1L) {
            Toast.makeText(this, "Nota guardada con éxito (ID: $idNota)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al guardar la nota", Toast.LENGTH_SHORT).show()
        }


    }
}