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

// Activity encargada de crear un nuevo animal
class CrearAnimalActivity : AppCompatActivity() {

    // Binding para acceder a los elementos del layout
    private lateinit var binding: ActivityCrearAnimalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding
        binding = ActivityCrearAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cambia el título del formulario
        binding.tvTituloFormulario.text = "➕ Crear Animal"

        // Evento del botón guardar
        binding.btnGuardar.setOnClickListener {
            crearAnimal()
        }
    }

    // Función que recoge los datos del formulario y crea el animal
    private fun crearAnimal() {

        // Obtiene los valores introducidos por el usuario
        val identificador = binding.etIdentificador.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()
        val sexo = binding.etSexo.text.toString().trim()
        val fecha = binding.etFecha.text.toString().trim()

        // Validación: comprueba que no haya campos vacíos
        if (identificador.isEmpty() || raza.isEmpty() || sexo.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crea el objeto que se enviará al backend
        val animal = AnimalRequest(
            identificador = identificador,
            raza = raza,
            sexo = sexo,
            fechaNacimiento = fecha,

            // Se asocia el animal a una granja (ID fijo = 1)
            granja = GranjaIdRequest(1)
        )

        // Obtiene la API de animales
        val api = RetrofitClient.getAnimalApi(this)

        // Lanza una corrutina en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Llamada a la API para crear el animal
                api.crearAnimal(animal)

                // Vuelve al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {

                    // Muestra mensaje de éxito
                    Toast.makeText(
                        this@CrearAnimalActivity,
                        "Animal creado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Cierra la pantalla y vuelve atrás
                    finish()
                }

            } catch (e: Exception) {
                // Manejo de errores
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CrearAnimalActivity,
                        "Error al crear animal: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}