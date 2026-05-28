package com.example.cattlemanager.eventosproductivos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityListaEventosProductivosBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.EventoProductivo
import com.example.cattlemanager.network.RetrofitClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaEventosProductivosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaEventosProductivosBinding
    private lateinit var tipo: String
    private lateinit var modo: String
    private var animalSeleccionado: Animal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaEventosProductivosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tipo = intent.getStringExtra("tipo") ?: "Productivo"
        modo = intent.getStringExtra("modo") ?: "REGISTRO"

        binding.tvTitulo.text = tipo
        binding.btnVolver.setOnClickListener { finish() }
        binding.recyclerEventos.layoutManager = LinearLayoutManager(this)

        if (modo == "ANIMAL") {
            mostrarSelectorAnimal()
        } else {
            cargarEventos()
        }
    }

    override fun onResume() {
        super.onResume()
        // Solo recargar si ya hay un animal seleccionado o estamos en modo REGISTRO
        if (modo == "REGISTRO" || animalSeleccionado != null) {
            cargarEventos()
        }
    }

    private fun mostrarSelectorAnimal() {
        val api = RetrofitClient.getAnimalApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animales = api.obtenerAnimales()
                withContext(Dispatchers.Main) {
                    if (animales.isEmpty()) {
                        Toast.makeText(this@ListaEventosProductivosActivity,
                            "No hay animales registrados", Toast.LENGTH_SHORT).show()
                        finish()
                        return@withContext
                    }
                    val opciones = animales.map { it.identificador }.toTypedArray()
                    MaterialAlertDialogBuilder(this@ListaEventosProductivosActivity)
                        .setTitle("Seleccionar animal")
                        .setItems(opciones) { _, which ->
                            animalSeleccionado = animales[which]
                            binding.tvTitulo.text = "$tipo · ${animalSeleccionado!!.identificador}"
                            cargarEventos()
                        }
                        .setOnCancelListener { finish() }
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ListaEventosProductivosActivity,
                        "Error al cargar animales", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun cargarEventos() {
        val api = RetrofitClient.getEventoProductivoApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var lista = api.obtenerEventos().filter { it.tipo == tipo }

                if (modo == "ANIMAL") {
                    val identificador = animalSeleccionado?.identificador
                    lista = lista.filter { it.animal?.identificador == identificador }
                }

                withContext(Dispatchers.Main) {
                    val n = lista.size
                    binding.tvContador.text = "$n ${if (n == 1) "registro" else "registros"}"
                    binding.recyclerEventos.adapter =
                        EventoProductivoAdapter(lista) { evento: EventoProductivo ->
                            val intent = Intent(
                                this@ListaEventosProductivosActivity,
                                DetalleEventoProductivoActivity::class.java
                            )
                            intent.putExtra("id", evento.id)
                            intent.putExtra("tipo", evento.tipo)
                            intent.putExtra("fecha", evento.fecha)
                            intent.putExtra("descripcion", evento.descripcion)
                            intent.putExtra("animal", evento.animal?.identificador ?: "Sin animal")
                            startActivity(intent)
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ListaEventosProductivosActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
