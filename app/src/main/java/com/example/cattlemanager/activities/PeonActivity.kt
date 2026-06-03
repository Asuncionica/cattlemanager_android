package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.alertas.CrearAlertaVeterinariaActivity
import com.example.cattlemanager.databinding.ActivityPeonBinding
import com.example.cattlemanager.eventosproductivos.EventoProductivoActivity
import com.example.cattlemanager.security.SessionManager
import com.example.cattlemanager.tareas.TareasPendientesActivity

class PeonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPeonBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: ""
        binding.tvNombreUsuario.text = nombreUsuario

        binding.btnAnimalesPeon.setOnClickListener {
            startActivity(Intent(this, AnimalActivity::class.java).apply {
                putExtra("rolUsuario", "PEON")
            })
        }
        binding.btnProduccionPeon.setOnClickListener {
            startActivity(Intent(this, EventoProductivoActivity::class.java))
        }
        binding.btnTareas.setOnClickListener {
            startActivity(Intent(this, TareasPendientesActivity::class.java))
        }
        binding.btnAlertasVet.setOnClickListener {
            startActivity(Intent(this, CrearAlertaVeterinariaActivity::class.java))
        }
        binding.btnCerrarSesion.setOnClickListener { cerrarSesion() }

        animarEntrada()
    }

    private fun animarEntrada() {
        val interp = DecelerateInterpolator()
        val offsetY = 70f * resources.displayMetrics.density

        listOf(
            binding.btnAnimalesPeon,
            binding.btnProduccionPeon,
            binding.btnTareas,
            binding.btnAlertasVet
        ).forEachIndexed { i, card ->
            card.alpha = 0f
            card.translationY = offsetY
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(420)
                .setStartDelay((i * 90).toLong())
                .setInterpolator(interp)
                .start()
        }

        binding.btnCerrarSesion.alpha = 0f
        binding.btnCerrarSesion.animate()
            .alpha(1f)
            .setDuration(300)
            .setStartDelay(460L)
            .setInterpolator(interp)
            .start()
    }

    private fun cerrarSesion() {
        sessionManager.clearSession()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
