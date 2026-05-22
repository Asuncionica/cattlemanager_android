package com.example.cattlemanager.eventossanitarios

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityDetalleEventoSanitarioBinding
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Muestra los datos completos de un evento sanitario recibidos por Intent; permite borrarlo
class DetalleEventoSanitarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleEventoSanitarioBinding
    private var eventoId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleEventoSanitarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventoId = intent.getLongExtra("id", 0)

        binding.btnVolver.setOnClickListener { finish() }

        binding.tvTipoDetalleSanitario.text = intent.getStringExtra("tipo") ?: ""
        binding.tvFechaDetalleSanitario.text = "Fecha: " + (intent.getStringExtra("fecha") ?: "")
        binding.tvDescripcionDetalleSanitario.text = "Descripción: " + (intent.getStringExtra("descripcion") ?: "")
        binding.tvAnimalDetalleSanitario.text = "Animal: " + (intent.getStringExtra("animal") ?: "Sin animal")

        binding.btnBorrarSanitario.setOnClickListener {
            confirmarBorrado()
        }
    }

    // Muestra un diálogo de confirmación antes de eliminar el registro
    private fun confirmarBorrado() {
        AlertDialog.Builder(this)
            .setTitle("Borrar evento sanitario")
            .setMessage("¿Seguro que quieres borrar este evento?")
            .setPositiveButton("Sí") { _, _ -> borrarEvento() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrarEvento() {
        val api = RetrofitClient.getEventoSanitarioApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.borrarEvento(eventoId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleEventoSanitarioActivity, "Evento borrado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleEventoSanitarioActivity, "Error al borrar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
