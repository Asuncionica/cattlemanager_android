package com.example.cattlemanager.usuarios

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityDetalleUsuarioBinding
import com.example.cattlemanager.model.Usuario
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.*

class DetalleUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleUsuarioBinding
    private var usuarioId: Long = 0
    private var rolIdActual: Long = 0
    private var passwordActual: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioId = intent.getLongExtra("id", 0)
        binding.btnVolver.setOnClickListener { finish() }
        binding.btnEditarUsuario.setOnClickListener {
            val intent = Intent(this, EditarUsuarioActivity::class.java)
            intent.putExtra("id", usuarioId)
            intent.putExtra("nombre", binding.tvNombre.text.toString())
            intent.putExtra("email", binding.tvEmail.text.toString().replace("Email: ", ""))
            intent.putExtra("rolId", rolIdActual)
            intent.putExtra("password", passwordActual)
            startActivity(intent)
        }

        binding.btnBorrarUsuario.setOnClickListener {
            confirmarBorrado()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarUsuario()
    }

    private fun cargarUsuario() {
        val api = RetrofitClient.getUsuarioApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val usuario = api.obtenerUsuarioPorId(usuarioId)

                withContext(Dispatchers.Main) {
                    mostrarUsuario(usuario)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mostrarUsuario(usuario: Usuario) {
        binding.tvNombre.text = usuario.nombre
        binding.tvEmail.text = "Email: ${usuario.email}"
        binding.tvRol.text = "Rol: ${usuario.rol.nombre}"
    }

    private fun confirmarBorrado() {
        AlertDialog.Builder(this)
            .setTitle("Borrar empleado")
            .setMessage("¿Seguro que quieres borrar este empleado?")
            .setPositiveButton("Sí") { _, _ ->
                borrarUsuario()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrarUsuario() {
        val api = RetrofitClient.getUsuarioApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.borrarUsuario(usuarioId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleUsuarioActivity, "Empleado borrado", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleUsuarioActivity, "Error al borrar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}