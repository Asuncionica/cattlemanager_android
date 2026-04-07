package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityVeterinarioBinding

// Activity principal para el rol VETERINARIO
// Ofrece acceso a funcionalidades sanitarias, reproductivas y de consulta
class VeterinarioActivity : AppCompatActivity() {

    // Binding para acceder a las vistas del layout
    private lateinit var binding: ActivityVeterinarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding
        binding = ActivityVeterinarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recoge el nombre del usuario desde el login
        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "Veterinario"

        // Muestra mensaje de bienvenida
        binding.tvBienvenidaVet.text = "Hola $nombreUsuario\nVeterinario"

        // Botón para funcionalidades sanitarias (no implementado aún)
        binding.btnSanitario.setOnClickListener {
            Toast.makeText(this, "Abrir pantalla de Tratamientos (pendiente)", Toast.LENGTH_SHORT).show()
        }

        // Botón para funcionalidades reproductivas (no implementado aún)
        binding.btnReproductivo.setOnClickListener {
            Toast.makeText(this, "Abrir pantalla de Reproducción (pendiente)", Toast.LENGTH_SHORT).show()
        }

        // Botón para producción (no implementado aún)
        binding.btnProduccionVet.setOnClickListener {
            Toast.makeText(this, "Abrir pantalla de Producción (pendiente)", Toast.LENGTH_SHORT).show()
        }

        // Botón para ver animales
        binding.btnAnimalesVet.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)

            // Se envía el rol para controlar permisos en otras pantallas
            intent.putExtra("rolUsuario", "VETERINARIO")

            startActivity(intent)
        }
    }
}