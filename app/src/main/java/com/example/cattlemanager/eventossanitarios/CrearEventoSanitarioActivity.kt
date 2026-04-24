package com.example.cattlemanager.eventossanitarios

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityCrearEventoSanitarioBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AnimalIdRequest
import com.example.cattlemanager.model.EventoSanitarioRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Formulario para registrar un nuevo evento sanitario vinculado a un animal
class CrearEventoSanitarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearEventoSanitarioBinding
    // Se guarda la lista completa para obtener el id del animal al guardar
    private var listaAnimales: List<Animal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearEventoSanitarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tiposSanitarios = listOf("Vacuna", "Desparasitación", "Tratamiento", "Revisión")
        val tipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposSanitarios)
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoSanitario.adapter = tipoAdapter

        val tipoPreseleccionado = intent.getStringExtra("tipo")
        if (tipoPreseleccionado != null) {
            val index = tiposSanitarios.indexOf(tipoPreseleccionado)
            if (index >= 0) binding.spinnerTipoSanitario.setSelection(index)
        }

        cargarAnimalesEnSpinner()

        binding.btnGuardarSanitario.setOnClickListener {
            crearEvento()
        }
    }

    // Obtiene los animales del backend y rellena el spinner con sus identificadores
    private fun cargarAnimalesEnSpinner() {
        val api = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animales = api.obtenerAnimales()
                listaAnimales = animales

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@CrearEventoSanitarioActivity,
                        android.R.layout.simple_spinner_item,
                        animales.map { it.identificador }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerAnimalSanitario.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoSanitarioActivity, "Error al cargar animales", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun crearEvento() {
        val tipo = binding.spinnerTipoSanitario.selectedItem.toString()
        val descripcion = binding.etDescripcionSanitario.text.toString().trim()
        val fecha = binding.etFechaSanitario.text.toString().trim()

        if (descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // No se puede crear si el spinner aún no ha cargado animales
        if (listaAnimales.isEmpty()) {
            Toast.makeText(this, "No hay animales disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val animalSeleccionado = listaAnimales[binding.spinnerAnimalSanitario.selectedItemPosition]

        val evento = EventoSanitarioRequest(
            tipo = tipo,
            descripcion = descripcion,
            fecha = fecha,
            animal = AnimalIdRequest(animalSeleccionado.id)
        )

        val api = RetrofitClient.getEventoSanitarioApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearEvento(evento)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoSanitarioActivity, "Evento creado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoSanitarioActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
