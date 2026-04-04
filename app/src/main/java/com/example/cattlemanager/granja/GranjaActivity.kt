package com.example.cattlemanager.granja

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityGranjaBinding
import com.example.cattlemanager.model.Granja
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.*

class GranjaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGranjaBinding
    private var granjaActual: Granja? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGranjaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEditarGranja.setOnClickListener {
            granjaActual?.let {
                val intent = Intent(this, EditarGranjaActivity::class.java)
                intent.putExtra("id", it.id)
                intent.putExtra("nombre", it.nombre)
                intent.putExtra("ubicacion", it.ubicacion)
                intent.putExtra("telefono", it.telefono)
                startActivity(intent)
            }
        }

        cargarGranja()
    }

    override fun onResume() {
        super.onResume()
        cargarGranja()
    }

    private fun cargarGranja() {
        val api = RetrofitClient.getGranjaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerGranjas()
                val granja = lista.firstOrNull()

                granjaActual = granja

                withContext(Dispatchers.Main) {
                    if (granja != null) {
                        binding.tvNombreGranja.text = granja.nombre
                        binding.tvUbicacion.text = "Ubicación: ${granja.ubicacion}"
                        binding.tvTelefono.text = "Teléfono: ${granja.telefono}"
                    } else {
                        Toast.makeText(this@GranjaActivity, "No hay granja", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}