package com.example.cattlemanager.granja

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    private var usuarioId: Long = -1L
    private var granjaId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarGranjaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioId = intent.getLongExtra("usuarioId", -1L)
        granjaId = intent.getLongExtra("id", -1L)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val ubicacion = intent.getStringExtra("ubicacion") ?: ""
        val telefono = intent.getStringExtra("telefono") ?: ""

        binding.etNombreGranja.setText(nombre)
        binding.etUbicacion.setText(ubicacion)
        binding.etTelefono.setText(telefono)

        binding.btnActualizarGranja.setOnClickListener {
            actualizarGranja()
        }

        binding.btnEliminarGranja.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar granja")
                .setMessage("¿Seguro que quieres eliminar esta granja?")
                .setPositiveButton("Sí") { _, _ ->
                    eliminarGranja()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun actualizarGranja() {
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
                api.actualizarGranja(usuarioId, granjaId, request)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditarGranjaActivity,
                        "Granja actualizada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditarGranjaActivity,
                        "Error al actualizar la granja",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun eliminarGranja() {
        val api = RetrofitClient.getGranjaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.eliminarGranja(usuarioId, granjaId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditarGranjaActivity,
                        "Granja eliminada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditarGranjaActivity,
                        "Error al eliminar la granja",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}