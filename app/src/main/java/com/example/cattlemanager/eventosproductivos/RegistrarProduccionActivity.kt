package com.example.cattlemanager.eventosproductivos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityRegistrarProduccionBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AnimalIdRequest
import com.example.cattlemanager.model.EventoProductivoRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrarProduccionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarProduccionBinding
    private var listaAnimales: List<Animal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarProduccionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarAnimalesEnSpinner()

        binding.btnGuardarProduccion.setOnClickListener {
            registrarProduccion()
        }
    }

    private fun cargarAnimalesEnSpinner() {
        val apiAnimal = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animales = apiAnimal.obtenerAnimales()
                listaAnimales = animales

                val identificadores = animales.map { it.identificador }

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@RegistrarProduccionActivity,
                        android.R.layout.simple_spinner_item,
                        identificadores
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerAnimalProduccion.adapter = adapter
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegistrarProduccionActivity,
                        "Error al cargar animales",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun registrarProduccion() {
        val tipo = binding.etTipoProduccion.text.toString().trim()
        val fecha = binding.etFechaProduccion.text.toString().trim()
        val descripcion = binding.etDescripcionProduccion.text.toString().trim()

        if (tipo.isEmpty() || fecha.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaAnimales.isEmpty()) {
            Toast.makeText(this, "No hay animales disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val posicionSeleccionada = binding.spinnerAnimalProduccion.selectedItemPosition
        val animalSeleccionado = listaAnimales[posicionSeleccionada]

        val request = EventoProductivoRequest(
            tipo = tipo,
            descripcion = descripcion,
            fecha = fecha,
            animal = AnimalIdRequest(animalSeleccionado.id)
        )

        val api = RetrofitClient.getEventoProductivoApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearEvento(request)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegistrarProduccionActivity,
                        "Producción registrada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegistrarProduccionActivity,
                        "Error al registrar producción",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}