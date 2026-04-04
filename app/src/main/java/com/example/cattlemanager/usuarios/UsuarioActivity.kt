package com.example.cattlemanager.usuarios

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityUsuariosBinding
import com.example.cattlemanager.model.Usuario
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.cattlemanager.usuarios.DetalleUsuarioActivity

class UsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsuariosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerUsuarios.layoutManager = LinearLayoutManager(this)

        binding.btnCrearUsuario.setOnClickListener {
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
                    binding.recyclerUsuarios.adapter = UsuarioAdapter(lista) { usuario: Usuario ->
                        val intent = Intent(this@UsuarioActivity, DetalleUsuarioActivity::class.java)
                        intent.putExtra("id", usuario.id)
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}