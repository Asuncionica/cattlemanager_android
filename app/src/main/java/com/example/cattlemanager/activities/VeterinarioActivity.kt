package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.alertas.AlertasVeterinariaActivity
import com.example.cattlemanager.databinding.ActivityVeterinarioBinding
import com.example.cattlemanager.eventosreproductivos.EventoReproductivoActivity
import com.example.cattlemanager.eventossanitarios.EventoSanitarioActivity
import com.example.cattlemanager.security.SessionManager

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
        binding.btnAlertas.setOnClickListener {
            startActivity(Intent(this, AlertasVeterinariaActivity::class.java))
        }
        binding.btnCerrarSesion.setOnClickListener { cerrarSesion() }

        animarEntrada()
    }

    private fun animarEntrada() {
        val interp = DecelerateInterpolator()
        val dp = resources.displayMetrics.density
        val offsetY = 70f * dp

        val cards = listOf(
            binding.btnAnimalesVet,
            binding.btnSanitario,
            binding.btnReproductivo,
            binding.btnAlertas
        )

        cards.forEachIndexed { i, card ->
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
        SessionManager(this).clearSession()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
