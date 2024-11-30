package com.example.proyecto_kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import java.time.LocalDate
import android.widget.DatePicker
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto_kotlin.modelos.Nota
import com.example.proyecto_kotlin.repositorio.NotaRepositorio

class CalendarioActivity : AppCompatActivity() {

    private val notaRepositorio = NotaRepositorio(this)

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        val botonEstadistica = findViewById<ImageButton>(R.id.botonEstadistica)
        val botonMenu = findViewById<ImageButton>(R.id.botonPrincipal)
        val botonBusqueda = findViewById<Button>(R.id.botonBusqueda)
        val textViewNota = findViewById<TextView>(R.id.textViewNota)

        val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getLong("id_usuario", -1L)

        if (idUsuario == -1L) {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
            return
        }

        botonBusqueda.setOnClickListener {
            val calendario = java.util.Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val fechaSeleccionada = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                    buscarNota(fechaSeleccionada, idUsuario, textViewNota)
                },
                calendario.get(java.util.Calendar.YEAR),
                calendario.get(java.util.Calendar.MONTH),
                calendario.get(java.util.Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        botonMenu.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }

        botonEstadistica.setOnClickListener {
            val intent = Intent(this, ContadorActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buscarNota(fecha: LocalDate, idUsuario: Long, textView: TextView) {
        val nota = notaRepositorio.obtenerNotaFecha(idUsuario, fecha)

        if (nota != null) {
            notaDialog(nota)
        } else {
            Toast.makeText(this, "No se encontró nota para esa fecha", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notaDialog(nota: Nota) {
        val mensaje = "Título: ${nota.titulo}\n\nTexto: ${nota.texto}\n\nEstado: ${nota.estado}"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nota del día")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()
    }
}
