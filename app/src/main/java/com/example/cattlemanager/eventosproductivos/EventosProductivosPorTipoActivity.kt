package com.example.cattlemanager.eventosproductivos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityEventosProductivosPorTipoBinding
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

        binding.cardVerPorAnimal.setOnClickListener {
            abrirLista("ANIMAL")
        }

        binding.cardVerPorRegistro.setOnClickListener {
            abrirLista("REGISTRO")
        }

        binding.cardCrearEvento.setOnClickListener {
            val intent = Intent(this, CrearEventoProductivoActivity::class.java)
            intent.putExtra("tipo", tipo)
            startActivity(intent)
        }

        cargarContador()
    }

    override fun onResume() {
        super.onResume()
        cargarContador()
    }

    private fun abrirLista(modo: String) {
        val intent = Intent(this, ListaEventosProductivosActivity::class.java)
        intent.putExtra("tipo", tipo)
        intent.putExtra("modo", modo)
        startActivity(intent)
    }

    private fun cargarContador() {
        val api = RetrofitClient.getEventoProductivoApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerEventos().filter { it.tipo == tipo }
                withContext(Dispatchers.Main) {
                    val n = lista.size
                    binding.tvContador.text =
                        "$n ${if (n == 1) "registro" else "registros"}"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EventosProductivosPorTipoActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
