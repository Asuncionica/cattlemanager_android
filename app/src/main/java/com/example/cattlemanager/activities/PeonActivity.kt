package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityPeonBinding

class PeonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPeonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "Peón"
        binding.tvBienvenidaPeon.text = "¡Hola $nombreUsuario! Bienvenido Peón"

        binding.btnAnimalesPeon.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            intent.putExtra("rolUsuario", "PEON")
            startActivity(intent)
        }
        binding.btnProduccionPeon.setOnClickListener {
            Toast.makeText(this, "Abrir Mis Tareas (pendiente)", Toast.LENGTH_SHORT).show()
        }
        binding.btnTareas.setOnClickListener {
            Toast.makeText(this, "Abrir Mis Tareas (pendiente)", Toast.LENGTH_SHORT).show()
        }

    }
}
