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

class EditarUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearUsuarioBinding
    private var usuarioId: Long = 0

    private val roles = listOf(
        "Veterinario",
        "Encargado",
        "Peón"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitulo.text = "Editar Empleado"
        binding.btnGuardarUsuario.text = "Actualizar"
        binding.btnVolver.setOnClickListener { finish() }

        usuarioId = intent.getLongExtra("id", 0)

        configurarSpinner()

        binding.etNombre.setText(intent.getStringExtra("nombre"))
        binding.etEmail.setText(intent.getStringExtra("email"))
        binding.etPassword.setText(intent.getStringExtra("password"))

        val rolId = intent.getLongExtra("rolId", 0)
        seleccionarRolEnSpinner(rolId)

        binding.btnGuardarUsuario.setOnClickListener {
            editarUsuario()
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

    private fun seleccionarRolEnSpinner(rolId: Long) {
        val posicion = when (rolId) {
            1L -> 0
            2L -> 1
            3L -> 2
            else -> 0
        }
        binding.spinnerRol.setSelection(posicion)
    }

    private fun editarUsuario() {
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
                api.actualizarUsuario(usuarioId, usuario)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarUsuarioActivity, "Empleado actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarUsuarioActivity, "Error al editar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}