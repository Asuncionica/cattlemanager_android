package com.example.cattlemanager.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityCrearAnimalBinding
import com.example.cattlemanager.model.AnimalRequest
import com.example.cattlemanager.model.GranjaIdRequest
import com.example.cattlemanager.model.GranjaRequest
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearAnimalBinding
    private var animalId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTituloFormulario.text = "✏️ Editar Animal"

        animalId = intent.getLongExtra("id", 0)

        binding.etIdentificador.setText(intent.getStringExtra("identificador"))
        binding.etRaza.setText(intent.getStringExtra("raza"))
        binding.etSexo.setText(intent.getStringExtra("sexo"))
        binding.etFecha.setText(intent.getStringExtra("fecha"))

        binding.btnGuardar.setOnClickListener {
            editarAnimal()
        }
    }

    private fun editarAnimal() {
        val identificador = binding.etIdentificador.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()
        val sexo = binding.etSexo.text.toString().trim()
        val fecha = binding.etFecha.text.toString().trim()

        if (identificador.isEmpty() || raza.isEmpty() || sexo.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val animal = AnimalRequest(
            identificador = identificador,
            raza = raza,
            sexo = sexo,
            fechaNacimiento = fecha,
            granja = GranjaIdRequest(1)
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
                    Toast.makeText(
                        this@EditarAnimalActivity,
                        "Error al editar animal: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}