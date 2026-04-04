package com.example.cattlemanager.eventosproductivos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityCrearEventoProductivoBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AnimalIdRequest
import com.example.cattlemanager.model.EventoProductivoRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearEventoProductivoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearEventoProductivoBinding
    private var listaAnimales: List<Animal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearEventoProductivoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarAnimalesEnSpinner()

        binding.btnGuardarEvento.setOnClickListener {
            crearEvento()
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
                        this@CrearEventoProductivoActivity,
                        android.R.layout.simple_spinner_item,
                        identificadores
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerAnimal.adapter = adapter
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CrearEventoProductivoActivity,
                        "Error al cargar animales",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun crearEvento() {
        val tipo = binding.etTipo.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val fecha = binding.etFecha.text.toString().trim()

        if (tipo.isEmpty() || descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaAnimales.isEmpty()) {
            Toast.makeText(this, "No hay animales disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val posicionSeleccionada = binding.spinnerAnimal.selectedItemPosition
        val animalSeleccionado = listaAnimales[posicionSeleccionada]

        val evento = EventoProductivoRequest(
            tipo = tipo,
            descripcion = descripcion,
            fecha = fecha,
            animal = AnimalIdRequest(animalSeleccionado.id)
        )

        val api = RetrofitClient.getEventoProductivoApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearEvento(evento)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CrearEventoProductivoActivity,
                        "Evento creado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CrearEventoProductivoActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}