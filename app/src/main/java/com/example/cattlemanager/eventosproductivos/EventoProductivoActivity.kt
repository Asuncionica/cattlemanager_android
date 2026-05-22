package com.example.cattlemanager.eventosproductivos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityEventosProductivosBinding

class EventoProductivoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosProductivosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosProductivosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        binding.cardLeche.setOnClickListener { navegarATipo("Leche") }
        binding.cardPesaje.setOnClickListener { navegarATipo("Pesaje") }
        binding.cardDestete.setOnClickListener { navegarATipo("Destete") }
        binding.cardVenta.setOnClickListener { navegarATipo("Venta") }
    }

    private fun navegarATipo(tipo: String) {
        val intent = Intent(this, EventosProductivosPorTipoActivity::class.java)
        intent.putExtra("tipo", tipo)
        startActivity(intent)
    }
}
