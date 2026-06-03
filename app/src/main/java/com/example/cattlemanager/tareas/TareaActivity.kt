package com.example.cattlemanager.tareas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityTareasBinding
import com.example.cattlemanager.model.Usuario
import com.example.cattlemanager.network.RetrofitClient
import com.example.cattlemanager.security.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTareasBinding
    private lateinit var sessionManager: SessionManager
    private var rolUsuario: String = "ENCARGADO"
    private var listaEmpleados: List<Usuario> = emptyList()
    private var selectedPeonId: Long = -1L
    private var selectedPeonNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTareasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        rolUsuario = intent.getStringExtra("rolUsuario") ?: "ENCARGADO"

        binding.btnVolver.setOnClickListener { finish() }

        if (rolUsuario == "PEON") {
            selectedPeonId = sessionManager.getUserId()
            binding.tvTituloTareas.text = "Mis Tareas"
            binding.tvEmpleadoSeleccionado.text = "Mis tareas"
            binding.cardSelectorEmpleado.isClickable = false
            binding.cardSelectorEmpleado.isFocusable = false
            binding.cardCrearTarea.visibility = View.GONE
        } else {
            cargarEmpleados()
        }

        binding.cardSelectorEmpleado.setOnClickListener { view ->
            if (listaEmpleados.isEmpty()) {
                Toast.makeText(this, "Cargando empleados…", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val menu = PopupMenu(this, view)
            listaEmpleados.forEachIndexed { i, emp ->
                menu.menu.add(0, i, i, emp.nombre)
            }
            menu.setOnMenuItemClickListener { item ->
                val emp = listaEmpleados[item.itemId]
                selectedPeonId = emp.id
                selectedPeonNombre = emp.nombre
                binding.tvEmpleadoSeleccionado.text = emp.nombre
                true
            }
            menu.show()
        }

        binding.cardVerTareas.setOnClickListener {
            if (selectedPeonId == -1L) {
                Toast.makeText(this, "Selecciona un empleado primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, TareasPendientesActivity::class.java)
            intent.putExtra("peonId", selectedPeonId)
            intent.putExtra("peonNombre", selectedPeonNombre)
            startActivity(intent)
        }

        binding.cardCrearTarea.setOnClickListener {
            startActivity(Intent(this, CrearTareaActivity::class.java))
        }
    }

    private fun cargarEmpleados() {
        val api = RetrofitClient.getUsuarioApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerUsuarios()
                withContext(Dispatchers.Main) {
                    listaEmpleados = lista
                    binding.tvContadorTareas.text =
                        "${lista.size} empleado${if (lista.size != 1) "s" else ""} registrado${if (lista.size != 1) "s" else ""}"
                    if (lista.isEmpty()) {
                        binding.tvEmpleadoSeleccionado.text = "Sin empleados registrados"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TareaActivity, "Error al cargar empleados", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
