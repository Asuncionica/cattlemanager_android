package com.example.cattlemanager.alertas

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cattlemanager.R
import com.example.cattlemanager.databinding.ActivityCrearAlertaVeterinariaBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AlertaVeterinariaRequest
import com.example.cattlemanager.model.AnimalIdRequest
import com.example.cattlemanager.model.UsuarioIdRequest
import com.example.cattlemanager.network.RetrofitClient
import com.example.cattlemanager.security.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearAlertaVeterinariaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearAlertaVeterinariaBinding
    private var listaAnimales: List<Animal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAlertaVeterinariaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        cargarAnimalesEnSpinner()

        binding.btnEnviarAlerta.setOnClickListener { enviarAlerta() }
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

    private fun cargarAnimalesEnSpinner() {
        val api = RetrofitClient.getAnimalApi(this)

        lifecycleScope.launch {
            try {
                val animales = withContext(Dispatchers.IO) { api.obtenerAnimales() }
                listaAnimales = animales

                val adapter = ArrayAdapter(
                    this@CrearAlertaVeterinariaActivity,
                    R.layout.spinner_item_white,
                    animales.map { it.identificador }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerAnimalAlerta.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CrearAlertaVeterinariaActivity, "Error al cargar animales", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarAlerta() {
        val descripcion = binding.etDescripcionAlerta.text.toString().trim()

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Describe el problema del animal", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaAnimales.isEmpty()) {
            Toast.makeText(this, "No hay animales disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val usuarioId = SessionManager(this).getUserId()
        if (usuarioId == 0L) {
            Toast.makeText(this, "Error: sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        val animal = listaAnimales[binding.spinnerAnimalAlerta.selectedItemPosition]

        val alerta = AlertaVeterinariaRequest(
            descripcion = descripcion,
            animal = AnimalIdRequest(animal.id),
            creadoPor = UsuarioIdRequest(usuarioId)
        )

        val api = RetrofitClient.getAlertaVeterinariaApi(this)

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) { api.crearAlerta(alerta) }
                Toast.makeText(this@CrearAlertaVeterinariaActivity, "Alerta enviada al veterinario", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CrearAlertaVeterinariaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
