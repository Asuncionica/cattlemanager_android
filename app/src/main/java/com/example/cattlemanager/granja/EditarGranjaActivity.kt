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
class EditarGranjaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarGranjaBinding
    private var id: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarGranjaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id = intent.getLongExtra("id", 0)
        binding.tvTituloFormularioGranja.text = "Editar Granja"
        binding.btnGuardar.text = "Actualizar"
        binding.etNombre.setText(intent.getStringExtra("nombre"))
        binding.etUbicacion.setText(intent.getStringExtra("ubicacion"))
        binding.etTelefono.setText(intent.getStringExtra("telefono"))
        binding.btnGuardar.setOnClickListener {
            actualizar()
        }
    }
    private fun actualizar() {
        val request = GranjaRequest(
            id = id,
            nombre = binding.etNombre.text.toString().trim(),
            ubicacion = binding.etUbicacion.text.toString().trim(),
            telefono = binding.etTelefono.text.toString().trim()
        )
        val api = RetrofitClient.getGranjaApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.actualizarGranja(id, request)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarGranjaActivity, "Granja actualizada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarGranjaActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}