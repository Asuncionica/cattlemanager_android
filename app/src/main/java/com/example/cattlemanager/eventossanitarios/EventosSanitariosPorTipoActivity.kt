package com.example.cattlemanager.eventossanitarios

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityEventosSanitariosPorTipoBinding
import com.example.cattlemanager.model.EventoSanitario
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventosSanitariosPorTipoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosSanitariosPorTipoBinding
    private lateinit var tipo: String
    private var animalId: Long = -1
    private var animalNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosSanitariosPorTipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tipo = intent.getStringExtra("tipo") ?: "Sanitario"
        animalId = intent.getLongExtra("animalId", -1)
        animalNombre = intent.getStringExtra("animalNombre") ?: ""

        binding.tvTitulo.text = if (animalNombre.isNotEmpty()) "$tipo · $animalNombre" else tipo

        binding.btnVolver.setOnClickListener { finish() }
        binding.recyclerSanitarios.layoutManager = LinearLayoutManager(this)

        binding.btnCrearSanitario.setOnClickListener {
            val intent = Intent(this, CrearEventoSanitarioActivity::class.java)
            intent.putExtra("tipo", tipo)
            if (animalId != -1L) intent.putExtra("animalId", animalId)
            startActivity(intent)
        }

        cargarEventos()
    }

    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    private fun cargarEventos() {
        val api = RetrofitClient.getEventoSanitarioApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerEventos()
                    .filter { it.tipo == tipo }
                    .filter { animalId == -1L || it.animal?.id == animalId }

                withContext(Dispatchers.Main) {
                    binding.tvContador.text = "${lista.size} evento${if (lista.size != 1) "s" else ""}"
                    binding.recyclerSanitarios.adapter = EventoSanitarioAdapter(lista) { evento: EventoSanitario ->
                        val intent = Intent(this@EventosSanitariosPorTipoActivity, DetalleEventoSanitarioActivity::class.java)
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
                    Toast.makeText(this@EventosSanitariosPorTipoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
