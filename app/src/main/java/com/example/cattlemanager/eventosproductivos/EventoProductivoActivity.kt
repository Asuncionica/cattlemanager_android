package com.example.cattlemanager.eventosproductivos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityEventosProductivosBinding
import com.example.cattlemanager.model.EventoProductivo
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventoProductivoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosProductivosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosProductivosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerEventos.layoutManager = LinearLayoutManager(this)

        binding.btnCrearEvento.setOnClickListener {
            startActivity(Intent(this, CrearEventoProductivoActivity::class.java))
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
                val lista = api.obtenerEventos()

                withContext(Dispatchers.Main) {
                    binding.recyclerEventos.adapter = EventoProductivoAdapter(lista) { evento: EventoProductivo ->
                        val intent = Intent(this@EventoProductivoActivity, DetalleEventoProductivoActivity::class.java)
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
            }
        }
    }
}