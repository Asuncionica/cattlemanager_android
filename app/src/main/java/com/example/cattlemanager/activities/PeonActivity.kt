package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityPeonBinding
import com.example.cattlemanager.eventosproductivos.ProduccionPeonActivity
import com.example.cattlemanager.eventosproductivos.RegistrarProduccionActivity

// Activity principal para el rol PEÓN
// Ofrece acceso limitado a funcionalidades de la app
class PeonActivity : AppCompatActivity() {

    // Binding para acceder a las vistas del layout
    private lateinit var binding: ActivityPeonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding
        binding = ActivityPeonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recoge el nombre del usuario enviado desde el login
        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "Peón"

        // Muestra mensaje de bienvenida personalizado
        binding.tvBienvenidaPeon.text = "¡Hola $nombreUsuario! Bienvenido Peón"

        // Botón para acceder a la lista de animales
        binding.btnAnimalesPeon.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)

            // Se envía el rol para limitar permisos en otras pantallas
            intent.putExtra("rolUsuario", "PEON")

            startActivity(intent)
        }

        // Botón de producción (funcionalidad aún no implementada)
        binding.btnProduccionPeon.setOnClickListener {
            startActivity(Intent(this, RegistrarProduccionActivity::class.java))
        }
        binding.btnVerProduccion.setOnClickListener {
            startActivity(Intent(this, ProduccionPeonActivity::class.java))
        }

        // Botón de tareas (funcionalidad aún no implementada)
        binding.btnTareas.setOnClickListener {
            Toast.makeText(this, "Abrir Mis Tareas (pendiente)", Toast.LENGTH_SHORT).show()
        }
    }
}