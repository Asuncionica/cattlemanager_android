package com.example.cattlemanager.eventosproductivos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityProduccionPeonBinding
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProduccionPeonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProduccionPeonBinding
    private lateinit var produccionAdapter: ProduccionAdapter

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProduccionPeonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        cargarProduccion()
    }

    private fun configurarRecyclerView() {
        produccionAdapter = ProduccionAdapter()

        binding.recyclerProduccion.apply {
            layoutManager = LinearLayoutManager(this@ProduccionPeonActivity)
            adapter = produccionAdapter
            setHasFixedSize(true)
        }
    }

    private fun cargarProduccion() {
        val api = RetrofitClient.getEventoProductivoApi(this)

        scope.launch {
            try {
                val lista = api.obtenerEventos()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProduccionPeonActivity,
                        "Eventos recibidos: ${lista.size}",
                        Toast.LENGTH_LONG
                    ).show()

                    produccionAdapter.actualizarLista(lista)
                }

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProduccionPeonActivity,
                        "Error al cargar producción: ${e.message ?: "desconocido"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}