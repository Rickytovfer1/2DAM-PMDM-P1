package com.example.proyecto_kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import java.time.LocalDate
import android.widget.DatePicker
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.proyecto_kotlin.BBDD.Sql
import com.example.proyecto_kotlin.modelos.Nota
import com.example.proyecto_kotlin.repositorio.NotaRepositorio
import com.example.proyecto_kotlin.repositorio.UsuarioRepositorio
import java.util.*

class CalendarioActivity : AppCompatActivity() {

    private val usuarioRepositorio: UsuarioRepositorio by lazy { UsuarioRepositorio(this) }
    private val notaRepositorio = NotaRepositorio(this)
    private lateinit var databaseHelper: Sql

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        val botonEstadistica = findViewById<ImageButton>(R.id.botonEstadistica)
        val botonMenu = findViewById<ImageButton>(R.id.botonPrincipal)
        val textViewNota = findViewById<TextView>(R.id.textViewNota)
        val calendarView = findViewById<com.applandeo.materialcalendarview.CalendarView>(R.id.calendario)
        val botonVolver: ImageButton = findViewById(R.id.botonVolver)

        val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getLong("id_usuario", -1L)

        if (idUsuario != -1L) {
            val layout = findViewById<ConstraintLayout>(R.id.calendarioActivity)
            layout?.let {
                usuarioRepositorio.aplicarColorFondo(idUsuario.toInt(), it)
            }
        }

        databaseHelper = Sql(this)
        val database = databaseHelper.writableDatabase

        botonMenu.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }

        botonEstadistica.setOnClickListener {
            val intent = Intent(this, ContadorActivity::class.java)
            startActivity(intent)
        }

        botonVolver.setOnClickListener {
            finish()
        }

        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                val clickedDate = LocalDate.of(
                    clickedDayCalendar.get(Calendar.YEAR),
                    clickedDayCalendar.get(Calendar.MONTH) + 1,
                    clickedDayCalendar.get(Calendar.DAY_OF_MONTH)
                )

                buscarNota(clickedDate, idUsuario, textViewNota)
            }
        })

        colorDatesBasedOnState(calendarView, idUsuario)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun colorDatesBasedOnState(calendarView: CalendarView, idUsuario: Long) {
        val calendarEvents = mutableListOf<EventDay>()
        val allDates = getAllDates()

        for (date in allDates) {
            val nota = notaRepositorio.obtenerNotaPorFecha(idUsuario, date)
            if (nota != null) {
                val calendar = Calendar.getInstance().apply {
                    set(date.year, date.monthValue - 1, date.dayOfMonth)
                }
                val drawable = when (nota.estado) {
                    "mal" -> ContextCompat.getDrawable(this@CalendarioActivity, R.drawable.background_mal)
                    "regular" -> ContextCompat.getDrawable(this@CalendarioActivity, R.drawable.background_regular)
                    "bien" -> ContextCompat.getDrawable(this@CalendarioActivity, R.drawable.background_bien)
                    else -> ContextCompat.getDrawable(this@CalendarioActivity, R.drawable.default_background)
                }
                drawable?.let {
                    calendarEvents.add(EventDay(calendar, it))
                }
            }
        }

        calendarView.setEvents(calendarEvents)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllDates(): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT fecha_creacion FROM notas", null)
        if (cursor.moveToFirst()) {
            do {
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion"))
                val localDate = LocalDate.parse(fecha)
                dates.add(localDate)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dates
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
        val mensaje = "Título: ${nota.titulo}\n\nTexto: ${nota.texto}"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nota del día")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("Eliminar") { dialog, _ ->
                AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro de que deseas eliminar esta nota?")
                    .setPositiveButton("Sí") { _, _ ->
                        nota.id?.let {
                            notaRepositorio.eliminarNota(it)
                            recreate()
                            Toast.makeText(this, "Nota eliminada", Toast.LENGTH_SHORT).show()
                        } ?: Toast.makeText(this, "Error: ID de nota es nulo", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { confirmDialog, _ -> confirmDialog.dismiss() }
                    .create()
                    .show()
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}