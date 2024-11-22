package com.example.proyecto_kotlin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AjustesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ajustes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharedPreferences = getSharedPreferences("ColorPreferences", MODE_PRIVATE)

        val botonBlanco = findViewById<Button>(R.id.botonBlanco)
        val botonRosa = findViewById<Button>(R.id.botonRosa)
        val botonRojo = findViewById<Button>(R.id.botonRojo)
        val botonVerde = findViewById<Button>(R.id.botonVerde)
        val botonEstadistica = findViewById<ImageButton>(R.id.botonEstadistica)
        val botonMenu = findViewById<ImageButton>(R.id.botonPrincipal)

        val layout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        val color = sharedPreferences.getInt("color", 0)

        botonBlanco.setOnClickListener {
            layout.setBackgroundColor(Color.WHITE)
        }

        botonRosa.setOnClickListener {
            layout.setBackgroundColor(Color.MAGENTA)
        }

        botonRojo.setOnClickListener {
            layout.setBackgroundColor(Color.RED)
        }

        botonVerde.setOnClickListener {
            layout.setBackgroundColor(Color.GREEN)
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

}