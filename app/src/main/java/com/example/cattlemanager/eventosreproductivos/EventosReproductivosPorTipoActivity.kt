package com.example.cattlemanager.eventosreproductivos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityEventosReproductivosPorTipoBinding
import com.example.cattlemanager.model.EventoReproductivo
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventosReproductivosPorTipoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosReproductivosPorTipoBinding
    private lateinit var tipo: String
    private var animalId: Long = -1
    private var animalNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosReproductivosPorTipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tipo = intent.getStringExtra("tipo") ?: "Reproductivo"
        animalId = intent.getLongExtra("animalId", -1)
        animalNombre = intent.getStringExtra("animalNombre") ?: ""

        binding.tvTitulo.text = if (animalNombre.isNotEmpty()) "$tipo · $animalNombre" else tipo

        binding.btnVolver.setOnClickListener { finish() }
        binding.recyclerReproductivos.layoutManager = LinearLayoutManager(this)

        binding.btnCrearReproductivo.setOnClickListener {
            val intent = Intent(this, CrearEventoReproductivoActivity::class.java)
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
        val api = RetrofitClient.getEventoReproductivoApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerEventos()
                    .filter { it.tipo == tipo }
                    .filter { animalId == -1L || it.animal?.id == animalId }

                withContext(Dispatchers.Main) {
                    binding.tvContador.text = "${lista.size} evento${if (lista.size != 1) "s" else ""}"
                    binding.recyclerReproductivos.adapter = EventoReproductivoAdapter(lista) { evento: EventoReproductivo ->
                        val intent = Intent(this@EventosReproductivosPorTipoActivity, DetalleEventoReproductivoActivity::class.java)
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
                    Toast.makeText(this@EventosReproductivosPorTipoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
