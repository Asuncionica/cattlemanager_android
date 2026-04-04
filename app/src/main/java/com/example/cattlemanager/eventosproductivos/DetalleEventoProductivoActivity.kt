package com.example.cattlemanager.eventosproductivos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityDetalleEventoProductivoBinding
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleEventoProductivoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleEventoProductivoBinding
    private var eventoId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleEventoProductivoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventoId = intent.getLongExtra("id", 0)

        binding.tvTipoDetalle.text = intent.getStringExtra("tipo") ?: ""
        binding.tvFechaDetalle.text = "Fecha: " + (intent.getStringExtra("fecha") ?: "")
        binding.tvDescripcionDetalle.text = "Descripción: " + (intent.getStringExtra("descripcion") ?: "")
        binding.tvAnimalDetalle.text = "Animal: " + (intent.getStringExtra("animal") ?: "Sin animal")

        binding.btnBorrarEvento.setOnClickListener {
            confirmarBorrado()
        }
    }

    private fun confirmarBorrado() {
        AlertDialog.Builder(this)
            .setTitle("Borrar evento")
            .setMessage("¿Seguro que quieres borrar este evento productivo?")
            .setPositiveButton("Sí") { _, _ ->
                borrarEvento()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrarEvento() {
        val api = RetrofitClient.getEventoProductivoApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.borrarEvento(eventoId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleEventoProductivoActivity, "Evento borrado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleEventoProductivoActivity, "Error al borrar evento", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}