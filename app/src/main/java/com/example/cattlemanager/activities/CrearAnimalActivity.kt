package com.example.cattlemanager.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityCrearAnimalBinding
import com.example.cattlemanager.model.AnimalRequest
import com.example.cattlemanager.model.GranjaIdRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearAnimalBinding
    private var granjaId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTituloFormulario.text = "Crear Animal"

        cargarGranja()

        binding.btnGuardar.setOnClickListener {
            crearAnimal()
        }
    }

    private fun cargarGranja() {
        val api = RetrofitClient.getGranjaApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val granjas = api.obtenerGranjas()
                if (granjas.isNotEmpty()) {
                    granjaId = granjas.first().id
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun crearAnimal() {
        val identificador = binding.etIdentificador.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()
        val sexo = binding.etSexo.text.toString().trim()
        val fecha = binding.etFecha.text.toString().trim()

        if (identificador.isEmpty() || raza.isEmpty() || sexo.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (granjaId == 0L) {
            Toast.makeText(this, "No se encontró ninguna granja", Toast.LENGTH_SHORT).show()
            return
        }

        val animal = AnimalRequest(
            identificador = identificador,
            raza = raza,
            sexo = sexo,
            fechaNacimiento = fecha,
            granja = GranjaIdRequest(granjaId)
        )

        val api = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.crearAnimal(animal)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearAnimalActivity, "Animal creado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearAnimalActivity, "Error al crear animal: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
