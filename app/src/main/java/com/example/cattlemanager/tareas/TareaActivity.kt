package com.example.cattlemanager.tareas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityTareasBinding
import com.example.cattlemanager.model.Tarea
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTareasBinding
    private var rolUsuario: String = "ENCARGADO"
    private var peonId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTareasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rolUsuario = intent.getStringExtra("rolUsuario") ?: "ENCARGADO"

        binding.btnVolver.setOnClickListener { finish() }

        if (rolUsuario == "PEON") {
            peonId = getSharedPreferences("app", MODE_PRIVATE).getLong("USUARIO_ID", 0L)
            binding.btnCrearTarea.visibility = View.GONE
            binding.tvTituloTareas.text = "Mis Tareas"
        } else {
            binding.btnCrearTarea.visibility = View.VISIBLE
        }

        binding.btnCrearTarea.setOnClickListener {
            startActivity(Intent(this, CrearTareaActivity::class.java))
        }

        cargarTareas()
    }

    override fun onResume() {
        super.onResume()
        cargarTareas()
    }

    private fun cargarTareas() {
        val api = RetrofitClient.getTareaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = if (rolUsuario == "PEON" && peonId != 0L) {
                    api.obtenerTareas().content.filter { it.peon?.id == peonId }
                } else {
                    api.obtenerTareas().content
                }

                withContext(Dispatchers.Main) {
                    binding.recyclerTareas.layoutManager = LinearLayoutManager(this@TareaActivity)
                    binding.recyclerTareas.adapter = TareaAdapter(
                        lista = lista,
                        onClick = { tarea ->
                            val intent = Intent(this@TareaActivity, DetalleTareaActivity::class.java)
                            intent.putExtra("id", tarea.id)
                            intent.putExtra("titulo", tarea.titulo)
                            intent.putExtra("descripcion", tarea.descripcion)
                            intent.putExtra("fechaVencimiento", tarea.fechaVencimiento)
                            intent.putExtra("completada", tarea.completada)
                            intent.putExtra("granjaId", tarea.granja?.id ?: 0L)
                            intent.putExtra("peonId", tarea.peon?.id ?: 0L)
                            intent.putExtra("peonNombre", tarea.peon?.nombre ?: "Sin asignar")
                            intent.putExtra("rolUsuario", rolUsuario)
                            startActivity(intent)
                        }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TareaActivity, "Error al cargar tareas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
