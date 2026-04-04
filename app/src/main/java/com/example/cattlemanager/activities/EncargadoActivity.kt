package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityEncargadoBinding
import com.example.cattlemanager.eventosproductivos.EventoProductivoActivity
import com.example.cattlemanager.granja.GranjaActivity
import com.example.cattlemanager.usuarios.UsuarioActivity

class EncargadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEncargadoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEncargadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "Encargado"
        binding.tvBienvenidaEnc.text = "¡Hola $nombreUsuario! Bienvenido Encargado"

        binding.btnAnimalesEnc.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            intent.putExtra("rolUsuario", "ENCARGADO")
            startActivity(intent)
        }

        binding.btnUsuarios.setOnClickListener {
            val intent = Intent(this, UsuarioActivity::class.java)
            startActivity(intent)
        }

        binding.btnProduccionEnc.setOnClickListener {
            startActivity(Intent(this, EventoProductivoActivity::class.java))
        }

        binding.btnGranja.setOnClickListener {
            startActivity(Intent(this, GranjaActivity::class.java))
        }
        }
    }
