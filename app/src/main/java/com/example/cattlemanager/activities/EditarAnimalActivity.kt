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

// Activity encargada de editar un animal existente
class EditarAnimalActivity : AppCompatActivity() {

    // Binding del layout reutilizado para crear/editar animales
    private lateinit var binding: ActivityCrearAnimalBinding

    // ID del animal que se va a editar
    private var animalId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding y muestra el layout
        binding = ActivityCrearAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cambia el título del formulario para indicar que es edición
        binding.tvTituloFormulario.text = "✏️ Editar Animal"

        // Recoge el ID del animal enviado desde la pantalla anterior
        animalId = intent.getLongExtra("id", 0)

        // Rellena los campos del formulario con los datos actuales del animal
        binding.etIdentificador.setText(intent.getStringExtra("identificador"))
        binding.etRaza.setText(intent.getStringExtra("raza"))
        binding.etSexo.setText(intent.getStringExtra("sexo"))
        binding.etFecha.setText(intent.getStringExtra("fecha"))

        // Al pulsar guardar, se ejecuta la edición
        binding.btnGuardar.setOnClickListener {
            editarAnimal()
        }
    }

    // Función que recoge los datos del formulario y actualiza el animal
    private fun editarAnimal() {

        // Obtiene los nuevos valores introducidos por el usuario
        val identificador = binding.etIdentificador.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()
        val sexo = binding.etSexo.text.toString().trim()
        val fecha = binding.etFecha.text.toString().trim()

        // Comprueba que no haya campos vacíos
        if (identificador.isEmpty() || raza.isEmpty() || sexo.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crea el objeto que se enviará al backend con los datos actualizados
        val animal = AnimalRequest(
            identificador = identificador,
            raza = raza,
            sexo = sexo,
            fechaNacimiento = fecha,

            // Actualmente se asigna siempre a la granja con ID 1
            granja = GranjaIdRequest(1)
        )

        // Obtiene la API de animales
        val api = RetrofitClient.getAnimalApi(this)

        // Ejecuta la llamada de red en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Llama a la API para actualizar el animal
                api.actualizarAnimal(animalId, animal)

                // Vuelve al hilo principal para mostrar mensajes en pantalla
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarAnimalActivity, "Animal actualizado", Toast.LENGTH_SHORT).show()

                    // Cierra la pantalla al terminar
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