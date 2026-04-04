package com.example.cattlemanager.usuarios

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityCrearUsuarioBinding
import com.example.cattlemanager.model.Rol
import com.example.cattlemanager.model.UsuarioRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearUsuarioBinding

    private val roles = listOf(
        "Veterinario",
        "Encargado",
        "Peón"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinner()

        binding.btnGuardarUsuario.setOnClickListener {
            crearUsuario()
        }
    }

    private fun configurarSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRol.adapter = adapter
    }

    private fun crearUsuario() {
        val nombre = binding.etNombre.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val rolSeleccionado = binding.spinnerRol.selectedItem.toString()
        val rolId = when (rolSeleccionado) {
            "Veterinario" -> 1L
            "Encargado" -> 2L
            "Peón" -> 3L
            else -> 3L
        }

        val usuario = UsuarioRequest(
            nombre = nombre,
            email = email,
            password = password,
            rol = Rol(rolId, "")
        )

        val api = RetrofitClient.getUsuarioApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearUsuario(usuario)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearUsuarioActivity, "Empleado creado", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearUsuarioActivity, "Error al crear", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}