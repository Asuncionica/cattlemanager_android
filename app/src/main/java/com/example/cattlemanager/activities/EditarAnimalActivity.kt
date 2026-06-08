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

class EditarAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearAnimalBinding
    private var animalId: Long = 0
    private var granjaId: Long = 0
    private var loteActualId: Long? = null
    private var listaLotes: List<LoteGeneticoResponse> = emptyList()
    private var usuarioHaTocadoSpinnerLote = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTituloFormulario.text = "Editar Animal"
        binding.btnVolver.setOnClickListener { finish() }

        animalId = intent.getLongExtra("id", 0)

        val sexoAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item_white,
            listOf("MACHO", "HEMBRA")
        )
        sexoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerSexo.adapter = sexoAdapter

        binding.etIdentificador.setText(intent.getStringExtra("identificador"))
        binding.etRaza.setText(intent.getStringExtra("raza"))
        val sexoActual = intent.getStringExtra("sexo")?.uppercase() ?: ""
        binding.spinnerSexo.setSelection(if (sexoActual.startsWith("H")) 1 else 0)
        binding.etFecha.setText(intent.getStringExtra("fecha"))
        binding.spinnerLoteGenetico.setOnTouchListener { _, _ ->
            usuarioHaTocadoSpinnerLote = true
            false
        }

        cargarGranja()
        cargarAnimalActual()
        cargarLotes()

        binding.btnGuardar.setOnClickListener {
            editarAnimal()
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

    private fun cargarAnimalActual() {
        val api = RetrofitClient.getAnimalApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animal = api.obtenerAnimalPorId(animalId)
                withContext(Dispatchers.Main) {
                    loteActualId = animal.loteGenetico?.id
                    seleccionarLoteActual()
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
                        this@EditarAnimalActivity,
                        R.layout.spinner_item_white,
                        nombres
                    )
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.spinnerLoteGenetico.adapter = adapter
                    seleccionarLoteActual()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun seleccionarLoteActual() {
        if (usuarioHaTocadoSpinnerLote) {
            return
        }
        val idActual = loteActualId ?: return
        val posicion = listaLotes.indexOfFirst { it.id == idActual }
        if (posicion >= 0) {
            binding.spinnerLoteGenetico.setSelection(posicion + 1)
        }
    }

    private fun editarAnimal() {
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
                api.actualizarAnimal(animalId, animal)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarAnimalActivity, "Animal actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarAnimalActivity, "Error al editar animal: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
