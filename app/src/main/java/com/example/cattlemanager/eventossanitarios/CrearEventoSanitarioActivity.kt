package com.example.cattlemanager.eventossanitarios

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
import com.example.cattlemanager.databinding.ActivityCrearEventoSanitarioBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.model.AnimalIdRequest
import com.example.cattlemanager.model.EventoSanitarioRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class CrearEventoSanitarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearEventoSanitarioBinding
    private var listaAnimales: List<Animal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearEventoSanitarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        val tiposSanitarios = listOf("Vacuna", "Desparasitación", "Tratamiento", "Revisión")
        val tipoAdapter = ArrayAdapter(this, R.layout.spinner_item_white, tiposSanitarios)
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoSanitario.adapter = tipoAdapter

        val tipoPreseleccionado = intent.getStringExtra("tipo")
        if (tipoPreseleccionado != null) {
            val index = tiposSanitarios.indexOf(tipoPreseleccionado)
            if (index >= 0) binding.spinnerTipoSanitario.setSelection(index)
        }

        binding.etFechaSanitario.setOnClickListener { mostrarDatePicker() }

        cargarAnimalesEnSpinner()

        binding.btnGuardarSanitario.setOnClickListener { crearEvento() }
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

    private fun mostrarDatePicker() {
        val cal = Calendar.getInstance()
        val locale = Locale("es", "ES")
        val config = resources.configuration
        config.setLocale(locale)
        val ctx = createConfigurationContext(config)
        DatePickerDialog(ctx, { _, year, month, day ->
            binding.etFechaSanitario.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun cargarAnimalesEnSpinner() {
        val api = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animales = api.obtenerAnimales()
                listaAnimales = animales

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@CrearEventoSanitarioActivity,
                        R.layout.spinner_item_white,
                        animales.map { it.identificador }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerAnimalSanitario.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoSanitarioActivity, "Error al cargar animales", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun crearEvento() {
        val tipo = binding.spinnerTipoSanitario.selectedItem.toString()
        val descripcion = binding.etDescripcionSanitario.text.toString().trim()
        val fecha = binding.etFechaSanitario.text.toString().trim()

        if (descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaAnimales.isEmpty()) {
            Toast.makeText(this, "No hay animales disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val animalSeleccionado = listaAnimales[binding.spinnerAnimalSanitario.selectedItemPosition]

        val evento = EventoSanitarioRequest(
            tipo = tipo,
            descripcion = descripcion,
            fecha = fecha,
            animal = AnimalIdRequest(animalSeleccionado.id)
        )

        val api = RetrofitClient.getEventoSanitarioApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearEvento(evento)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoSanitarioActivity, "Evento creado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearEventoSanitarioActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
