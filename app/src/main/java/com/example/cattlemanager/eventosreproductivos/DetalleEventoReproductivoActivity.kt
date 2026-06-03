package com.example.cattlemanager.eventosreproductivos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityDetalleEventoReproductivoBinding
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Muestra los datos completos de un evento reproductivo recibidos por Intent; permite borrarlo
class DetalleEventoReproductivoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleEventoReproductivoBinding
    private var eventoId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleEventoReproductivoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventoId = intent.getLongExtra("id", 0)

        binding.btnVolver.setOnClickListener { finish() }

        binding.tvTipoDetalleReproductivo.text = intent.getStringExtra("tipo") ?: ""
        binding.tvFechaDetalleReproductivo.text = "Fecha: " + (intent.getStringExtra("fecha") ?: "")
        binding.tvDescripcionDetalleReproductivo.text = "Descripción: " + (intent.getStringExtra("descripcion") ?: "")
        binding.tvAnimalDetalleReproductivo.text = "Animal: " + (intent.getStringExtra("animal") ?: "Sin animal")

        binding.btnBorrarReproductivo.setOnClickListener {
            confirmarBorrado()
        }
    }

    // Muestra un diálogo de confirmación antes de eliminar el registro
    private fun confirmarBorrado() {
        AlertDialog.Builder(this)
            .setTitle("Borrar evento reproductivo")
            .setMessage("¿Seguro que quieres borrar este evento?")
            .setPositiveButton("Sí") { _, _ -> borrarEvento() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrarEvento() {
        val api = RetrofitClient.getEventoReproductivoApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.borrarEvento(eventoId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleEventoReproductivoActivity, "Evento borrado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleEventoReproductivoActivity, "Error al borrar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
