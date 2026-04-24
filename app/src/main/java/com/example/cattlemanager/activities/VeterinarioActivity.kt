package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.alertas.AlertasVeterinariaActivity
import com.example.cattlemanager.databinding.ActivityVeterinarioBinding
import com.example.cattlemanager.eventosreproductivos.EventoReproductivoActivity
import com.example.cattlemanager.eventossanitarios.EventoSanitarioActivity

class VeterinarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVeterinarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVeterinarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: ""
        binding.tvNombreUsuario.text = nombreUsuario

        binding.btnAnimalesVet.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            intent.putExtra("rolUsuario", "VETERINARIO")
            startActivity(intent)
        }
        binding.btnSanitario.setOnClickListener {
            startActivity(Intent(this, EventoSanitarioActivity::class.java))
        }
        binding.btnReproductivo.setOnClickListener {
            startActivity(Intent(this, EventoReproductivoActivity::class.java))
        }
        // Accede a las alertas que encargado/peón han enviado sobre animales
        binding.btnAlertas.setOnClickListener {
            startActivity(Intent(this, AlertasVeterinariaActivity::class.java))
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
