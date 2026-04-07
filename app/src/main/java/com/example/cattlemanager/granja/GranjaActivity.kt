package com.example.cattlemanager.granja

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityGranjaBinding
import com.example.cattlemanager.model.Granja
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class GranjaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGranjaBinding
    private lateinit var adapter: GranjaAdapter
    private var usuarioId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGranjaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioId = intent.getLongExtra("usuarioId", -1L)

        if (usuarioId == -1L) {
            Toast.makeText(this, "Error: usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        adapter = GranjaAdapter(emptyList()) { granja ->
            val intent = Intent(this, EditarGranjaActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("id", granja.id)
            intent.putExtra("nombre", granja.nombre)
            intent.putExtra("ubicacion", granja.ubicacion)
            intent.putExtra("telefono", granja.telefono)
            startActivity(intent)
        }

        binding.recyclerGranjas.layoutManager = LinearLayoutManager(this)
        binding.recyclerGranjas.adapter = adapter

        binding.btnCrearGranja.setOnClickListener {
            val intent = Intent(this, CrearGranjaActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            startActivity(intent)
        }

        cargarGranjas()
    }

    override fun onResume() {
        super.onResume()
        cargarGranjas()
    }

    private fun cargarGranjas() {
        val api = RetrofitClient.getGranjaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerGranjasPorUsuario(usuarioId)

                withContext(Dispatchers.Main) {
                    if (lista.isEmpty()) {
                        binding.tvMensajeSinGranjas.visibility = View.VISIBLE
                        binding.recyclerGranjas.visibility = View.GONE
                    } else {
                        binding.tvMensajeSinGranjas.visibility = View.GONE
                        binding.recyclerGranjas.visibility = View.VISIBLE
                        adapter.actualizarLista(lista)
                    }
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@GranjaActivity,
                        "HTTP ${e.code()} al cargar granjas",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@GranjaActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}