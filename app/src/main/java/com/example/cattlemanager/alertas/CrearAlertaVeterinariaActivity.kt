package com.example.cattlemanager.alertas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cattlemanager.databinding.ActivityCrearAlertaVeterinariaBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AlertaVeterinariaRequest
import com.example.cattlemanager.model.AnimalIdRequest
import com.example.cattlemanager.model.UsuarioIdRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        lifecycleScope.launch {
            try {
                val animales = withContext(Dispatchers.IO) { api.obtenerAnimales() }
                listaAnimales = animales

                val adapter = ArrayAdapter(
                    this@CrearAlertaVeterinariaActivity,
                    android.R.layout.simple_spinner_item,
                    animales.map { it.identificador }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerAnimalAlerta.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CrearAlertaVeterinariaActivity, "Error al cargar animales", Toast.LENGTH_SHORT).show()
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

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) { api.crearAlerta(alerta) }
                Toast.makeText(this@CrearAlertaVeterinariaActivity, "Alerta enviada al veterinario", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CrearAlertaVeterinariaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
