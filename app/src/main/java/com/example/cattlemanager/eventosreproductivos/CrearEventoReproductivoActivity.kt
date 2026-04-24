package com.example.cattlemanager.eventosreproductivos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityCrearEventoReproductivoBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AnimalIdRequest
import com.example.cattlemanager.model.EventoReproductivoRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearEventoReproductivoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearEventoReproductivoBinding
    private var listaAnimales: List<Animal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearEventoReproductivoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tiposReproductivos = listOf("Parto", "Inseminación", "Celo", "Revisión")
        val tipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposReproductivos)
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoReproductivo.adapter = tipoAdapter

        val tipoPreseleccionado = intent.getStringExtra("tipo")
        if (tipoPreseleccionado != null) {
            val index = tiposReproductivos.indexOf(tipoPreseleccionado)
            if (index >= 0) binding.spinnerTipoReproductivo.setSelection(index)
        }

        cargarAnimalesEnSpinner()

        binding.btnGuardarReproductivo.setOnClickListener {
            crearEvento()
        }
    }

    private fun cargarAnimalesEnSpinner() {
        val api = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animales = api.obtenerAnimales()
                listaAnimales = animales

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@CrearEventoReproductivoActivity,
                        android.R.layout.simple_spinner_item,
                        animales.map { it.identificador }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerAnimalReproductivo.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoReproductivoActivity, "Error al cargar animales", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun crearEvento() {
        val tipo = binding.spinnerTipoReproductivo.selectedItem.toString()
        val descripcion = binding.etDescripcionReproductivo.text.toString().trim()
        val fecha = binding.etFechaReproductivo.text.toString().trim()

        if (descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaAnimales.isEmpty()) {
            Toast.makeText(this, "No hay animales disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val animalSeleccionado = listaAnimales[binding.spinnerAnimalReproductivo.selectedItemPosition]

        val evento = EventoReproductivoRequest(
            tipo = tipo,
            descripcion = descripcion,
            fecha = fecha,
            animal = AnimalIdRequest(animalSeleccionado.id)
        )

        val api = RetrofitClient.getEventoReproductivoApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearEvento(evento)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoReproductivoActivity, "Evento creado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoReproductivoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
