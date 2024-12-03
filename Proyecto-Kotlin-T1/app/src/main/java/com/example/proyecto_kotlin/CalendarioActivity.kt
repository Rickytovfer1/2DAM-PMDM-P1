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
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.proyecto_kotlin.BBDD.Sql
import com.example.proyecto_kotlin.ContadorActivity
import com.example.proyecto_kotlin.PrincipalActivity
import com.example.proyecto_kotlin.R
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
        val botonBusqueda = findViewById<Button>(R.id.botonBusqueda)
        val textViewNota = findViewById<TextView>(R.id.textViewNota)
        val calendarView = findViewById<CalendarView>(R.id.calendario)

        val sharedPreferences = getSharedPreferences("idUsuario", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getLong("id_usuario", -1L)

        if (idUsuario != -1L) {
            val layout = findViewById<ConstraintLayout>(R.id.calendarioActivity)
            layout?.let {
                usuarioRepositorio.aplicarColorFondo(idUsuario.toInt(), it)
            }
        }

        databaseHelper = Sql(this) // Instanciar la clase Sql
        val database = databaseHelper.writableDatabase // Obtener base de datos escribible

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

        // Set up calendar interactions
        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                val clickedDate = LocalDate.of(
                    clickedDayCalendar.get(Calendar.YEAR),
                    clickedDayCalendar.get(Calendar.MONTH) + 1,
                    clickedDayCalendar.get(Calendar.DAY_OF_MONTH)
                )
                val nota = notaRepositorio.obtenerNotaPorFecha(idUsuario, clickedDate)
                if (nota != null) {
                    Toast.makeText(applicationContext, "Estado: ${nota.estado}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "No hay nota para esta fecha", Toast.LENGTH_SHORT).show()
                }
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
        val db = databaseHelper.readableDatabase // Usar databaseHelper para obtener base de datos de solo lectura
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
        val mensaje = "Título: ${nota.titulo}\n\nTexto: ${nota.texto}\n\nEstado: ${nota.estado}"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nota del día")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()
    }
}