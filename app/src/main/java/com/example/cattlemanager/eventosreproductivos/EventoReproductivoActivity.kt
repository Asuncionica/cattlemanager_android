package com.example.cattlemanager.eventosreproductivos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityEventosReproductivosBinding

class EventoReproductivoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosReproductivosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosReproductivosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        binding.cardParto.setOnClickListener { navegarATipo("Parto") }
        binding.cardInseminacion.setOnClickListener { navegarATipo("Inseminación") }
        binding.cardCelo.setOnClickListener { navegarATipo("Celo") }
        binding.cardRevision.setOnClickListener { navegarATipo("Revisión") }
    }

    private fun navegarATipo(tipo: String) {
        val intent = Intent(this, EventosReproductivosPorTipoActivity::class.java)
        intent.putExtra("tipo", tipo)
        startActivity(intent)
    }
}
