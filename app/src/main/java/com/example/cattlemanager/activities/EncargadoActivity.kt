package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityEncargadoBinding
import com.example.cattlemanager.eventosproductivos.EventoProductivoActivity
import com.example.cattlemanager.granja.GranjaActivity
import com.example.cattlemanager.usuarios.UsuarioActivity

// Activity principal para el rol ENCARGADO
// Desde aquí el usuario accede a las distintas funcionalidades de la app
class EncargadoActivity : AppCompatActivity() {

    // Binding para acceder a las vistas del layout
    private lateinit var binding: ActivityEncargadoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding
        binding = ActivityEncargadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recoge datos del usuario enviados desde el login
        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "Encargado"
        val usuarioId = intent.getLongExtra("usuarioId", -1)

        // Muestra mensaje de bienvenida personalizado
        binding.tvBienvenidaEnc.text = "¡Hola $nombreUsuario! Bienvenido Encargado. ID: $usuarioId"

        // Botón para ir a la gestión de animales
        binding.btnAnimalesEnc.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)

            // Se envía el rol para controlar permisos en otras pantallas
            intent.putExtra("rolUsuario", "ENCARGADO")

            // Se envía el ID del usuario
            intent.putExtra("usuarioId", usuarioId)

            startActivity(intent)
        }

        // Botón para gestionar usuarios
        binding.btnUsuarios.setOnClickListener {
            val intent = Intent(this, UsuarioActivity::class.java)

            // Se pasa el ID del usuario
            intent.putExtra("usuarioId", usuarioId)

            startActivity(intent)
        }

        // Botón para acceder a eventos productivos
        binding.btnProduccionEnc.setOnClickListener {
            val intent = Intent(this, EventoProductivoActivity::class.java)

            // Se pasa el ID del usuario
            intent.putExtra("usuarioId", usuarioId)

            startActivity(intent)
        }

        // Botón para acceder a la gestión de granjas
        binding.btnGranja.setOnClickListener {
            val intent = Intent(this, GranjaActivity::class.java)

            // Se pasa el ID del usuario
            intent.putExtra("usuarioId", usuarioId)

            startActivity(intent)
        }
    }
}