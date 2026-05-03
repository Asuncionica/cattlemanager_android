package com.example.cattlemanager.granja
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityEditarGranjaBinding
import com.example.cattlemanager.model.GranjaRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class CrearGranjaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarGranjaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarGranjaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnVolver.setOnClickListener { finish() }
        binding.tvTituloFormularioGranja.text = "Crear Granja"
        binding.btnGuardar.text = "Crear"
        binding.btnGuardar.setOnClickListener {
            crearGranja()
        }
    }
    private fun crearGranja() {
        val nombre = binding.etNombre.text.toString().trim()
        val ubicacion = binding.etUbicacion.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        if (nombre.isEmpty() || ubicacion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        val request = GranjaRequest(
            id = 0,
            nombre = nombre,
            ubicacion = ubicacion,
            telefono = telefono
        )
        val api = RetrofitClient.getGranjaApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearGranja(request)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearGranjaActivity, "Granja creada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearGranjaActivity, "Error al crear la granja", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}