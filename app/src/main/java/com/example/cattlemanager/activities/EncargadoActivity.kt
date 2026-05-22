package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.alertas.CrearAlertaVeterinariaActivity
import com.example.cattlemanager.databinding.ActivityEncargadoBinding
import com.example.cattlemanager.eventosproductivos.EventoProductivoActivity
import com.example.cattlemanager.granja.GranjaActivity
import com.example.cattlemanager.security.SessionManager
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
            startActivity(Intent(this, AnimalActivity::class.java).apply {
                putExtra("rolUsuario", "ENCARGADO")
            })
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
        binding.btnTareas.setOnClickListener {
            startActivity(Intent(this, TareaActivity::class.java).apply {
                putExtra("rolUsuario", "ENCARGADO")
            })
        }
        binding.btnLotesGeneticos.setOnClickListener {
            startActivity(Intent(this, LotesGeneticosActivity::class.java))
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
            binding.btnAnimalesEnc,
            binding.btnUsuarios,
            binding.btnProduccionEnc,
            binding.btnGranja,
            binding.btnTareas,
            binding.btnLotesGeneticos,
            binding.btnAlertasVet
        ).forEachIndexed { i, card ->
            card.alpha = 0f
            card.translationY = offsetY
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(420)
                .setStartDelay((i * 70).toLong())
                .setInterpolator(interp)
                .start()
        }

        binding.btnCerrarSesion.alpha = 0f
        binding.btnCerrarSesion.animate()
            .alpha(1f)
            .setDuration(300)
            .setStartDelay(520L)
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
