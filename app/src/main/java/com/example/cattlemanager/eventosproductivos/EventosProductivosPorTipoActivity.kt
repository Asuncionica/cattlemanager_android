package com.example.cattlemanager.eventosproductivos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityEventosProductivosPorTipoBinding
import com.example.cattlemanager.model.EventoProductivo
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventosProductivosPorTipoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosProductivosPorTipoBinding
    private lateinit var tipo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosProductivosPorTipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tipo = intent.getStringExtra("tipo") ?: "Productivo"
        binding.tvTitulo.text = tipo

        binding.btnVolver.setOnClickListener { finish() }

        binding.recyclerEventos.layoutManager = LinearLayoutManager(this)

        binding.btnCrearEvento.setOnClickListener {
            val intent = Intent(this, CrearEventoProductivoActivity::class.java)
            intent.putExtra("tipo", tipo)
            startActivity(intent)
        }

        cargarEventos()
    }

    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    private fun cargarEventos() {
        val api = RetrofitClient.getEventoProductivoApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerEventos().filter { it.tipo == tipo }

                withContext(Dispatchers.Main) {
                    binding.recyclerEventos.adapter = EventoProductivoAdapter(lista) { evento: EventoProductivo ->
                        val intent = Intent(this@EventosProductivosPorTipoActivity, DetalleEventoProductivoActivity::class.java)
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
                    Toast.makeText(this@EventosProductivosPorTipoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
