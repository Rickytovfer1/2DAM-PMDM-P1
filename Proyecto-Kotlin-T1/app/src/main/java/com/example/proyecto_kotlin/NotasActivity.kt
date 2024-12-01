package com.example.proyecto_kotlin

import android.annotation.SuppressLint
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
import com.example.proyecto_kotlin.repositorio.NotaRepositorio
import java.time.LocalDate

class NotasActivity : AppCompatActivity()
{
    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
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
        val editTextTitulo = findViewById<EditText>(R.id.editTextTituloNota)

        val botonMal = findViewById<ImageButton>(R.id.botonMal)
        val botonRegular = findViewById<ImageButton>(R.id.botonRegular)
        val botonBien = findViewById<ImageButton>(R.id.botonBien)

        val botonEstadistica = findViewById<ImageButton>(R.id.botonEstadistica)
        val botonMenu = findViewById<ImageButton>(R.id.botonPrincipal)
        val botonGuardar = findViewById<Button>(R.id.botonGuardar)

        val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getLong("id_usuario", -1L)

        val sharedPreferencesNota = getSharedPreferences("nota_$idUsuario", MODE_PRIVATE)
        val textoNotaGuardada = sharedPreferencesNota.getString("textoNota", "")
        val tituloNotaGuardado = sharedPreferencesNota.getString("tituloNota", "")

        val notaRepositorio = NotaRepositorio(this)

        editTextNota.setText(textoNotaGuardada)
        editTextTitulo.setText(tituloNotaGuardado)

        val fechaActual = LocalDate.now()
        val notaExistente = notaRepositorio.obtenerNotaPorFecha(idUsuario, fechaActual)

        val sharedPreferencesFecha = getSharedPreferences("ultimaFecha_$idUsuario", MODE_PRIVATE)
        val ultimaFechaGuardada = sharedPreferencesFecha.getString("ultimaFecha", null)
        val fechaActualString = LocalDate.now().toString()

        var estadoNota = ""

        if (ultimaFechaGuardada != fechaActualString) {

            botonGuardar.text = getString(R.string.guardar)
            editTextTitulo.text.clear()
            editTextNota.text.clear()

            val editorFecha = sharedPreferencesFecha.edit()
            editorFecha.putString("ultimaFecha", fechaActualString)
            editorFecha.apply()
        } else {

            if (notaExistente != null) {
                botonGuardar.text = getString(R.string.modificarNota)
                editTextTitulo.setText(notaExistente.titulo)
                editTextNota.setText(notaExistente.texto)
            } else {
                botonGuardar.text = getString(R.string.guardar)
            }
        }

        botonMal.setOnClickListener {
            estadoNota = "mal"
            botonMal.isEnabled = false
            botonRegular.isEnabled = false
            botonBien.isEnabled = false
        }

        botonRegular.setOnClickListener {
            estadoNota = "regular"
            botonMal.isEnabled = false
            botonRegular.isEnabled = false
            botonBien.isEnabled = false
        }

        botonBien.setOnClickListener {
            estadoNota = "bien"
            botonMal.isEnabled = false
            botonRegular.isEnabled = false
            botonBien.isEnabled = false
        }

        botonGuardar.setOnClickListener {
            val textoNota = editTextNota.text.toString()
            val tituloNotaTitulo = editTextTitulo.text.toString()
            val fechaCreacion = LocalDate.now()

            if (estadoNota == "") {
                Toast.makeText(this, "Por favor, selecciona un estado para la nota", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (textoNota.isBlank()) {
                Toast.makeText(this, "Por favor, escribe algo en la nota", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (idUsuario == -1L) {
                Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sharedPreferencesNo = getSharedPreferences("nota_$idUsuario", MODE_PRIVATE)
            val editor = sharedPreferencesNo.edit()
            editor.putString("textoNota", textoNota)
            editor.putString("tituloNota", tituloNotaTitulo)
            editor.apply()



            if (editTextNota.text.isNotBlank()) {
                editTextNota.isFocusable = false
                editTextNota.isFocusableInTouchMode = false
                editTextNota.isEnabled = false
            }

            if (editTextTitulo.text.isNotBlank()) {
                editTextTitulo.isFocusable = false
                editTextTitulo.isFocusableInTouchMode = false
                editTextTitulo.isEnabled = false
            }

            guardarOModificar(
                notaRepositorio,
                tituloNotaTitulo,
                textoNota,
                estadoNota,
                fechaCreacion,
                idUsuario
            )

            botonGuardar.text = getString(R.string.modificarNota)

            botonGuardar.isEnabled = false

            val editorFecha = sharedPreferencesFecha.edit()
            editorFecha.putString("ultimaFecha", fechaActualString)
            editorFecha.apply()
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

    fun guardarOModificar(
        notaRepositorio: NotaRepositorio,
        titulo: String,
        texto: String,
        estado: String,
        fechaCreacion: LocalDate,
        idUsuario: Long) {
        val notaYaExiste = notaRepositorio.modificarNota(idUsuario, titulo, texto, fechaCreacion, estado)

        if (notaYaExiste) {
            Toast.makeText(this, "Nota actualizada con éxito", Toast.LENGTH_SHORT).show()
        } else {
            val nota = Nota(
                titulo = titulo,
                texto = texto,
                estado = estado,
                fechaCreacion = fechaCreacion,
                idUsuario = idUsuario
            )

            val idNota = notaRepositorio.insertarNota(nota)
            if (idNota != -1L) {
                Toast.makeText(this, "Nota guardada con éxito (ID: $idNota)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al guardar la nota", Toast.LENGTH_SHORT).show()
            }
        }
    }
}