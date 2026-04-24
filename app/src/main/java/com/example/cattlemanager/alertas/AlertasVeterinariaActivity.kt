package com.example.cattlemanager.alertas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityAlertasVeterinariaBinding
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Pantalla del veterinario: ve las alertas que le han enviado encargado/peón
class AlertasVeterinariaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertasVeterinariaBinding

    // Controla si el filtro activo es "solo pendientes" o "todas"
    private var mostrarSoloPendientes = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertasVeterinariaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }
        binding.recyclerAlertas.layoutManager = LinearLayoutManager(this)

        binding.btnFiltrarPendientes.setOnClickListener {
            mostrarSoloPendientes = true
            cargarAlertas()
        }
        binding.btnFiltrarTodas.setOnClickListener {
            mostrarSoloPendientes = false
            cargarAlertas()
        }

        cargarAlertas()
    }

    override fun onResume() {
        super.onResume()
        cargarAlertas()
    }

    private fun cargarAlertas() {
        val api = RetrofitClient.getAlertaVeterinariaApi(this)
        val filtro: Boolean? = if (mostrarSoloPendientes) false else null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerAlertas(filtro)

                withContext(Dispatchers.Main) {
                    // Muestra el contador de pendientes en la cabecera
                    val pendientes = lista.count { !it.atendida }
                    if (pendientes > 0) {
                        binding.tvContadorPendientes.text = "$pendientes pendiente${if (pendientes > 1) "s" else ""}"
                        binding.tvContadorPendientes.visibility = View.VISIBLE
                    } else {
                        binding.tvContadorPendientes.visibility = View.GONE
                    }

                    binding.recyclerAlertas.adapter = AlertaVeterinariaAdapter(lista) { alerta ->
                        atenderAlerta(alerta.id)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AlertasVeterinariaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun atenderAlerta(id: Long) {
        val api = RetrofitClient.getAlertaVeterinariaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.marcarAtendida(id)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AlertasVeterinariaActivity, "Alerta marcada como atendida", Toast.LENGTH_SHORT).show()
                    cargarAlertas()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AlertasVeterinariaActivity, "Error al atender: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
