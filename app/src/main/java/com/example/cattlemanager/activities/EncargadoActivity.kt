package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.alertas.CrearAlertaVeterinariaActivity
import com.example.cattlemanager.databinding.ActivityEncargadoBinding
import com.example.cattlemanager.eventosproductivos.EventoProductivoActivity
import com.example.cattlemanager.granja.GranjaActivity
import com.example.cattlemanager.tareas.TareaActivity
import com.example.cattlemanager.usuarios.UsuarioActivity

class EncargadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEncargadoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEncargadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: ""
        binding.tvNombreUsuario.text = nombreUsuario

        binding.btnAnimalesEnc.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            intent.putExtra("rolUsuario", "ENCARGADO")
            startActivity(intent)
        }
        binding.btnUsuarios.setOnClickListener {
            startActivity(Intent(this, UsuarioActivity::class.java))
        }
        binding.btnProduccionEnc.setOnClickListener {
            startActivity(Intent(this, EventoProductivoActivity::class.java))
        }
        binding.btnGranja.setOnClickListener {
            startActivity(Intent(this, GranjaActivity::class.java))
        }
        // El encargado gestiona y asigna tareas a los peones
        binding.btnTareas.setOnClickListener {
            val intent = Intent(this, TareaActivity::class.java)
            intent.putExtra("rolUsuario", "ENCARGADO")
            startActivity(intent)
        }
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
