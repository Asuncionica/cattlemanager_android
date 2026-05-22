package com.example.cattlemanager.eventossanitarios

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityEventosSanitariosBinding

class EventoSanitarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosSanitariosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosSanitariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        binding.cardVacuna.setOnClickListener { navegarATipo("Vacuna") }
        binding.cardDesparasitacion.setOnClickListener { navegarATipo("Desparasitación") }
        binding.cardTratamiento.setOnClickListener { navegarATipo("Tratamiento") }
        binding.cardRevision.setOnClickListener { navegarATipo("Revisión") }
    }

    private fun navegarATipo(tipo: String) {
        val intent = Intent(this, EventosSanitariosPorTipoActivity::class.java)
        intent.putExtra("tipo", tipo)
        startActivity(intent)
    }
}
