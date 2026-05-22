package com.example.cattlemanager.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityAltaAnimalBinding
import com.example.cattlemanager.model.AnimalRequest
import com.example.cattlemanager.model.GranjaIdRequest
import com.example.cattlemanager.model.LoteGeneticoIdRequest
import com.example.cattlemanager.model.LoteGeneticoResponse
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AltaAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAltaAnimalBinding
    private var listaLotes: List<LoteGeneticoResponse> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAltaAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa el catálogo de lotes desde el servidor
        cargarLotesDisponibles()

        // Asigna el evento click para procesar y enviar el formulario
        binding.btnGuardarAnimal.setOnClickListener {
            ejecutarPersistenciaAnimal()
        }
    }

    private fun cargarLotesDisponibles() {
        val loteApi = RetrofitClient.getLoteGeneticoApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lotesBackend = loteApi.getLotesGeneticos()

                withContext(Dispatchers.Main) {
                    listaLotes = lotesBackend

                    // Mapeamos los nombres para mostrarlos en el Spinner UI
                    val nombresLotes = lotesBackend.map { it.nombre }.toMutableList()
                    nombresLotes.add(0, "Sin lote genético asignado")

                    val adapter = ArrayAdapter(
                        this@AltaAnimalActivity,
                        android.R.layout.simple_spinner_item,
                        nombresLotes
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerLoteGenetico.adapter = adapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AltaAnimalActivity, "Error al sincronizar catálogo de lotes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun ejecutarPersistenciaAnimal() {
        val identificador = binding.etIdentificador.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()

        // Validación previa de campos obligatorios
        if (identificador.isEmpty() || raza.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el índice seleccionado y extraer únicamente el ID relacional del lote
        val seleccionIdx = binding.spinnerLoteGenetico.selectedItemPosition
        val loteGenetico = if (seleccionIdx == 0) {
            null
        } else {
            listaLotes[seleccionIdx - 1].id?.let { LoteGeneticoIdRequest(it) }
        }

        // Fecha formateada en ISO estándar para el backend
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 1. Construcción obligatoria del objeto interno GranjaIdRequest
        val granjaRequest = GranjaIdRequest(id = 1L)

        // 2. Construcción del DTO de subida correcto (AnimalRequest) con sus datos reales
        val nuevoAnimalRequest = AnimalRequest(
            identificador = identificador,
            raza = raza,
            sexo = "HEMBRA", // Regla de negocio por defecto para la ficha básica de producción
            fechaNacimiento = fechaActual,
            granja = granjaRequest, // Inyección del objeto requerido por tu data class
            loteGenetico = loteGenetico
        )

        val animalApi = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Despacho asíncrono seguro a Spring Boot usando el DTO mapeado
                animalApi.crearAnimal(nuevoAnimalRequest)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AltaAnimalActivity, "Animal registrado con éxito", Toast.LENGTH_SHORT).show()
                    finish() // Retorna al inventario principal
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AltaAnimalActivity, "Fallo en persistencia: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
