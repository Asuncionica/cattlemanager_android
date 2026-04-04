package com.example.cattlemanager.granja

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityEditarGranjaBinding
import com.example.cattlemanager.model.GranjaRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.*

class EditarGranjaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarGranjaBinding
    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarGranjaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getLongExtra("id", 0)

        binding.etNombre.setText(intent.getStringExtra("nombre"))
        binding.etUbicacion.setText(intent.getStringExtra("ubicacion"))
        binding.etTelefono.setText(intent.getStringExtra("telefono"))

        binding.btnGuardar.setOnClickListener {
            actualizar()
        }
    }

    private fun actualizar() {
        val request = GranjaRequest(
            nombre = binding.etNombre.text.toString(),
            ubicacion = binding.etUbicacion.text.toString(),
            telefono = binding.etTelefono.text.toString()
        )

        val api = RetrofitClient.getGranjaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.actualizarGranja(id, request)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarGranjaActivity, "Actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}