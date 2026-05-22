package com.example.cattlemanager.tareas

import android.app.DatePickerDialog
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.R
import com.example.cattlemanager.databinding.ActivityCrearTareaBinding
import com.example.cattlemanager.model.GranjaIdRequest
import com.example.cattlemanager.model.TareaRequest
import com.example.cattlemanager.model.Usuario
import com.example.cattlemanager.model.UsuarioIdRequest
import com.example.cattlemanager.network.RetrofitClient
import com.example.cattlemanager.util.DateFormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class CrearTareaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearTareaBinding
    private var granjaId: Long = 0L
    private var listaPeones: List<Usuario> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearTareaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }
        configurarCampoFecha()
        cargarDatosIniciales()

        binding.btnGuardarTarea.setOnClickListener {
            guardarTarea()
        }
    }

    private fun configurarCampoFecha() {
        binding.etFechaTarea.keyListener = null
        binding.etFechaTarea.isFocusable = false
        binding.etFechaTarea.isClickable = true
        binding.etFechaTarea.setOnClickListener { mostrarSelectorFecha() }
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

    private fun mostrarSelectorFecha() {
        val hoy = Calendar.getInstance()
        val locale = Locale("es", "ES")
        val config = resources.configuration
        config.setLocale(locale)
        val ctx = createConfigurationContext(config)
        DatePickerDialog(ctx, { _, year, month, dayOfMonth ->
            val fechaFormateada = String.format(
                Locale("es", "ES"),
                "%02d/%02d/%04d",
                dayOfMonth,
                month + 1,
                year
            )
            binding.etFechaTarea.setText(fechaFormateada)
        }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun cargarDatosIniciales() {
        val apiGranja = RetrofitClient.getGranjaApi(this)
        val apiUsuario = RetrofitClient.getUsuarioApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val granjas = apiGranja.obtenerGranjas()
                val granja = granjas.firstOrNull()
                if (granja != null) granjaId = granja.id

                // Filtra solo los usuarios con rol peón (id = 3)
                val todosUsuarios = apiUsuario.obtenerUsuarios()
                val peones = todosUsuarios.filter { it.rol.id == 3L }
                listaPeones = peones

                withContext(Dispatchers.Main) {
                    if (peones.isEmpty()) {
                        Toast.makeText(this@CrearTareaActivity, "No hay peones registrados", Toast.LENGTH_SHORT).show()
                    }
                    val nombres = peones.map { it.nombre }
                    val adapter = ArrayAdapter(
                        this@CrearTareaActivity,
                        R.layout.spinner_item_white,
                        nombres
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerPeon.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearTareaActivity, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun guardarTarea() {
        val titulo = binding.etTituloTarea.text.toString().trim()
        val descripcion = binding.etDescripcionTarea.text.toString().trim()
        val fecha = binding.etFechaTarea.text.toString().trim()

        if (titulo.isEmpty() || descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (granjaId == 0L) {
            Toast.makeText(this, "No se pudo obtener la granja", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaPeones.isEmpty()) {
            Toast.makeText(this, "No hay peones disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val peonSeleccionado = listaPeones[binding.spinnerPeon.selectedItemPosition]

        val tarea = TareaRequest(
            titulo = titulo,
            descripcion = descripcion,
            // La UI trabaja en formato español, pero el backend sigue recibiendo ISO.
            fechaVencimiento = DateFormatUtils.toBackend(fecha),
            completada = false,
            granja = GranjaIdRequest(granjaId),
            peon = UsuarioIdRequest(peonSeleccionado.id)
        )

        val api = RetrofitClient.getTareaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearTarea(tarea)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearTareaActivity, "Tarea creada correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearTareaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
