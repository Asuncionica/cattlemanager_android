package com.example.cattlemanager.granja

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityCrearGranjaBinding
import com.example.cattlemanager.model.GranjaRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearGranjaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearGranjaBinding
    private var usuarioId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearGranjaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioId = intent.getLongExtra("usuarioId", -1L)

        binding.btnGuardarGranja.setOnClickListener {
            crearGranja()
        }
    }

    private fun crearGranja() {
        val nombre = binding.etNombreGranja.text.toString().trim()
        val ubicacion = binding.etUbicacion.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()

        if (nombre.isEmpty() || ubicacion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val request = GranjaRequest(
            nombre = nombre,
            ubicacion = ubicacion,
            telefono = telefono
        )

        val api = RetrofitClient.getGranjaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearGranja(usuarioId, request)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CrearGranjaActivity,
                        "Granja creada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CrearGranjaActivity,
                        "Error al crear la granja",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}