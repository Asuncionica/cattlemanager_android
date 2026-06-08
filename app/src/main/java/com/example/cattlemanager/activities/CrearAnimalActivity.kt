package com.example.cattlemanager.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.R
import com.example.cattlemanager.databinding.ActivityCrearAnimalBinding
import com.example.cattlemanager.model.AnimalRequest
import com.example.cattlemanager.model.GranjaIdRequest
import com.example.cattlemanager.model.LoteGeneticoResponse
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearAnimalBinding
    private var granjaId: Long = 0
    private var listaLotes: List<LoteGeneticoResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTituloFormulario.text = "Crear Animal"
        binding.btnVolver.setOnClickListener { finish() }

        val sexoAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item_white,
            listOf("MACHO", "HEMBRA")
        )
        sexoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerSexo.adapter = sexoAdapter

        cargarGranja()
        cargarLotes()

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

    private fun cargarLotes() {
        val api = RetrofitClient.getLoteGeneticoApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lotes = api.getLotesGeneticos()
                withContext(Dispatchers.Main) {
                    listaLotes = lotes
                    val nombres = lotes.map { it.nombre }.toMutableList()
                    nombres.add(0, "Sin lote genético")

                    val adapter = ArrayAdapter(
                        this@CrearAnimalActivity,
                        R.layout.spinner_item_white,
                        nombres
                    )
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.spinnerLoteGenetico.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun crearAnimal() {
        val identificador = binding.etIdentificador.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()
        val sexo = binding.spinnerSexo.selectedItem.toString()
        val fecha = binding.etFecha.text.toString().trim()

        if (identificador.isEmpty() || raza.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (granjaId == 0L) {
            Toast.makeText(this, "No se encontró ninguna granja", Toast.LENGTH_SHORT).show()
            return
        }

        val seleccionLote = binding.spinnerLoteGenetico.selectedItemPosition
        val loteGeneticoId = if (seleccionLote == 0) {
            null
        } else {
            listaLotes[seleccionLote - 1].id
        }

        val animal = AnimalRequest(
            identificador = identificador,
            raza = raza,
            sexo = sexo,
            fechaNacimiento = fecha,
            granja = GranjaIdRequest(granjaId),
            loteGeneticoId = loteGeneticoId
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
