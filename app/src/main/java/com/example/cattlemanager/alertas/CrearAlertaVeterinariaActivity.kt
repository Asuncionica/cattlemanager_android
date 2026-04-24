package com.example.cattlemanager.alertas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityCrearAlertaVeterinariaBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AlertaVeterinariaRequest
import com.example.cattlemanager.model.AnimalIdRequest
import com.example.cattlemanager.model.UsuarioIdRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Pantalla de encargado o peón para enviar una alerta al veterinario sobre un animal
class CrearAlertaVeterinariaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearAlertaVeterinariaBinding
    private var listaAnimales: List<Animal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAlertaVeterinariaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        cargarAnimalesEnSpinner()

        binding.btnEnviarAlerta.setOnClickListener {
            enviarAlerta()
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
                        this@CrearAlertaVeterinariaActivity,
                        android.R.layout.simple_spinner_item,
                        animales.map { it.identificador }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerAnimalAlerta.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearAlertaVeterinariaActivity, "Error al cargar animales", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun enviarAlerta() {
        val descripcion = binding.etDescripcionAlerta.text.toString().trim()

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Describe el problema del animal", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaAnimales.isEmpty()) {
            Toast.makeText(this, "No hay animales disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        // El id del usuario logueado se guardó en SharedPreferences al hacer login
        val usuarioId = getSharedPreferences("app", MODE_PRIVATE).getLong("USUARIO_ID", 0L)
        if (usuarioId == 0L) {
            Toast.makeText(this, "Error: sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        val animal = listaAnimales[binding.spinnerAnimalAlerta.selectedItemPosition]

        val alerta = AlertaVeterinariaRequest(
            descripcion = descripcion,
            animal = AnimalIdRequest(animal.id),
            creadoPor = UsuarioIdRequest(usuarioId)
        )

        val api = RetrofitClient.getAlertaVeterinariaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearAlerta(alerta)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearAlertaVeterinariaActivity, "Alerta enviada al veterinario", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearAlertaVeterinariaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
