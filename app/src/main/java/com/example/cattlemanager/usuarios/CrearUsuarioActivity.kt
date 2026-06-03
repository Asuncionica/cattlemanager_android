package com.example.cattlemanager.usuarios

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.R
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

    private val roles = listOf("Veterinario", "Encargado", "Peón")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }
        configurarSpinner()

        binding.btnGuardarUsuario.setOnClickListener { crearUsuario() }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun configurarSpinner() {
        val adapter = ArrayAdapter(this, R.layout.spinner_item_white, roles)
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
