package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityVeterinarioBinding

class VeterinarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVeterinarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVeterinarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "Veterinario"
        binding.tvBienvenidaVet.text = "Hola $nombreUsuario\nVeterinario"


        binding.btnSanitario.setOnClickListener {
            Toast.makeText(this, "Abrir pantalla de Tratamientos (pendiente)", Toast.LENGTH_SHORT).show()
        }
        binding.btnReproductivo.setOnClickListener {
            Toast.makeText(this, "Abrir pantalla de Reproducción (pendiente)", Toast.LENGTH_SHORT).show()
        }
        binding.btnProduccionVet.setOnClickListener {
            Toast.makeText(this, "Abrir pantalla de Producción (pendiente)", Toast.LENGTH_SHORT).show()
        }
        binding.btnAnimalesVet.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            intent.putExtra("rolUsuario", "VETERINARIO")
            startActivity(intent)
        }
    }
}
