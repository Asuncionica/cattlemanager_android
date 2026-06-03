package com.example.cattlemanager.usuarios

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityUsuariosBinding
import com.example.cattlemanager.model.Usuario
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsuariosBinding
    private var listaUsuarios: List<Usuario> = emptyList()
    private var usuarioSeleccionado: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        binding.cardSelectorEmpleado.setOnClickListener { view ->
            if (listaUsuarios.isEmpty()) {
                Toast.makeText(this, "Cargando empleados…", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val menu = PopupMenu(this, view)
            listaUsuarios.forEachIndexed { i, u ->
                menu.menu.add(0, i, i, "${u.nombre} (${u.rol.nombre})")
            }
            menu.setOnMenuItemClickListener { item ->
                usuarioSeleccionado = listaUsuarios[item.itemId]
                binding.tvEmpleadoSeleccionado.text = usuarioSeleccionado?.nombre
                true
            }
            menu.show()
        }

        binding.cardVerEmpleado.setOnClickListener {
            val u = usuarioSeleccionado
            if (u == null) {
                Toast.makeText(this, "Selecciona un empleado primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, DetalleUsuarioActivity::class.java)
            intent.putExtra("id", u.id)
            startActivity(intent)
        }

        binding.cardNuevoEmpleado.setOnClickListener {
            startActivity(Intent(this, CrearUsuarioActivity::class.java))
        }

        cargarUsuarios()
    }

    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        val api = RetrofitClient.getUsuarioApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerUsuarios()
                withContext(Dispatchers.Main) {
                    listaUsuarios = lista
                    val n = lista.size
                    binding.tvContadorUsuarios.text =
                        "$n empleado${if (n != 1) "s" else ""} registrado${if (n != 1) "s" else ""}"
                    if (lista.isEmpty()) {
                        binding.tvEmpleadoSeleccionado.text = "Sin empleados registrados"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UsuarioActivity, "Error al cargar empleados", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
