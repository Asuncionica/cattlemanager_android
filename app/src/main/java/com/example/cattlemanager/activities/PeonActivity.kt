package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.alertas.CrearAlertaVeterinariaActivity
import com.example.cattlemanager.databinding.ActivityPeonBinding
import com.example.cattlemanager.eventosproductivos.EventoProductivoActivity
import com.example.cattlemanager.tareas.TareaActivity

class PeonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPeonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: ""
        binding.tvNombreUsuario.text = nombreUsuario

        binding.btnAnimalesPeon.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            intent.putExtra("rolUsuario", "PEON")
            startActivity(intent)
        }
        binding.btnProduccionPeon.setOnClickListener {
            startActivity(Intent(this, EventoProductivoActivity::class.java))
        }
        binding.btnTareas.setOnClickListener {
            val intent = Intent(this, TareaActivity::class.java)
            intent.putExtra("rolUsuario", "PEON")
            startActivity(intent)
        }
        // El peón puede avisar al veterinario si detecta un problema en un animal
        binding.btnAlertasVet.setOnClickListener {
            startActivity(Intent(this, CrearAlertaVeterinariaActivity::class.java))
        }

        binding.btnCerrarSesion.setOnClickListener { cerrarSesion() }
    }

    private fun cerrarSesion() {
        getSharedPreferences("app", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
