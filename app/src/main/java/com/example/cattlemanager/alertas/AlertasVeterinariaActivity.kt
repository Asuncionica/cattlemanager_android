package com.example.cattlemanager.alertas

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityAlertasVeterinariaBinding
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertasVeterinariaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertasVeterinariaBinding
    private var mostrarSoloPendientes = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertasVeterinariaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }
        binding.recyclerAlertas.layoutManager = LinearLayoutManager(this)

        binding.btnFiltrarPendientes.setOnClickListener {
            mostrarSoloPendientes = true
            actualizarBotonesFiltro()
            cargarAlertas()
        }
        binding.btnFiltrarTodas.setOnClickListener {
            mostrarSoloPendientes = false
            actualizarBotonesFiltro()
            cargarAlertas()
        }

        actualizarBotonesFiltro()
        cargarAlertas()
    }

    override fun onResume() {
        super.onResume()
        cargarAlertas()
    }

    private fun actualizarBotonesFiltro() {
        if (mostrarSoloPendientes) {
            binding.btnFiltrarPendientes.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF6F00"))
            binding.btnFiltrarTodas.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BDBDBD"))
        } else {
            binding.btnFiltrarPendientes.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BDBDBD"))
            binding.btnFiltrarTodas.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2E7D32"))
        }
    }

    private fun cargarAlertas() {
        val api = RetrofitClient.getAlertaVeterinariaApi(this)
        val atendida: Boolean? = if (mostrarSoloPendientes) false else null

        lifecycleScope.launch {
            try {
                val lista = withContext(Dispatchers.IO) { api.obtenerAlertas(atendida) }

                val pendientes = lista.count { !it.atendida }
                if (pendientes > 0) {
                    binding.tvContadorPendientes.text = "$pendientes pendiente${if (pendientes > 1) "s" else ""}"
                    binding.tvContadorPendientes.visibility = View.VISIBLE
                } else {
                    binding.tvContadorPendientes.visibility = View.GONE
                }

                if (lista.isEmpty()) {
                    binding.recyclerAlertas.visibility = View.GONE
                    binding.tvSinAlertas.visibility = View.VISIBLE
                } else {
                    binding.tvSinAlertas.visibility = View.GONE
                    binding.recyclerAlertas.visibility = View.VISIBLE
                    binding.recyclerAlertas.adapter = AlertaVeterinariaAdapter(lista) { alerta ->
                        atenderAlerta(alerta.id)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AlertasVeterinariaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun atenderAlerta(id: Long) {
        val api = RetrofitClient.getAlertaVeterinariaApi(this)

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) { api.marcarAtendida(id) }
                Toast.makeText(this@AlertasVeterinariaActivity, "Alerta marcada como atendida", Toast.LENGTH_SHORT).show()
                cargarAlertas()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AlertasVeterinariaActivity, "Error al atender: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
