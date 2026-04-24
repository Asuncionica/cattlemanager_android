package com.example.cattlemanager.tareas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityTareasPendientesBinding
import com.example.cattlemanager.model.Tarea
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareasPendientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTareasPendientesBinding
    private var peonId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTareasPendientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        peonId = intent.getLongExtra(
            "peonId",
            getSharedPreferences("app", MODE_PRIVATE).getLong("USUARIO_ID", 0L)
        )

        binding.recyclerTareasPendientes.layoutManager = LinearLayoutManager(this)

        cargarTareasPendientes()
    }

    override fun onResume() {
        super.onResume()
        cargarTareasPendientes()
    }

    private fun cargarTareasPendientes() {
        // Si no hay sesión válida, mostramos estado vacío para evitar una pantalla en blanco.
        if (peonId == 0L) {
            binding.tvContadorPendientes.text = "0"
            binding.layoutSinTareas.visibility = View.VISIBLE
            binding.recyclerTareasPendientes.visibility = View.GONE
            Toast.makeText(this, "No se ha podido identificar al peón", Toast.LENGTH_SHORT).show()
            return
        }

        val api = RetrofitClient.getTareaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pendientes = api.obtenerTareas().content.filter {
                    it.peon?.id == peonId && !it.completada
                }

                withContext(Dispatchers.Main) {
                    binding.tvContadorPendientes.text = pendientes.size.toString()

                    if (pendientes.isEmpty()) {
                        binding.layoutSinTareas.visibility = View.VISIBLE
                        binding.recyclerTareasPendientes.visibility = View.GONE
                    } else {
                        binding.layoutSinTareas.visibility = View.GONE
                        binding.recyclerTareasPendientes.visibility = View.VISIBLE
                        binding.recyclerTareasPendientes.adapter =
                            TareaAdapter(pendientes) { tarea: Tarea ->
                                val intent = Intent(
                                    this@TareasPendientesActivity,
                                    DetalleTareaActivity::class.java
                                )
                                intent.putExtra("id", tarea.id)
                                intent.putExtra("titulo", tarea.titulo)
                                intent.putExtra("descripcion", tarea.descripcion)
                                intent.putExtra("fechaVencimiento", tarea.fechaVencimiento)
                                intent.putExtra("completada", tarea.completada)
                                intent.putExtra("granjaId", tarea.granja?.id ?: 0L)
                                intent.putExtra("peonId", tarea.peon?.id ?: 0L)
                                intent.putExtra("peonNombre", tarea.peon?.nombre ?: "")
                                intent.putExtra("rolUsuario", "PEON")
                                startActivity(intent)
                            }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TareasPendientesActivity, "Error al cargar tareas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
