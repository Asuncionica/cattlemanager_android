package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.adapter.AnimalAdapter
import com.example.cattlemanager.databinding.ActivityAnimalesBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Activity que muestra la lista de animales
class AnimalActivity : AppCompatActivity() {

    // Binding para acceder a las vistas del layout sin usar findViewById
    private lateinit var binding: ActivityAnimalesBinding

    // Variable que guarda el rol del usuario (ENCARGADO, PEON, etc.)
    private var rolUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding con el layout correspondiente
        binding = ActivityAnimalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recoge el rol del usuario enviado desde otra Activity
        rolUsuario = intent.getStringExtra("rolUsuario") ?: ""

        // Configura el RecyclerView con un layout vertical
        binding.recyclerAnimales.layoutManager = LinearLayoutManager(this)

        // Si el usuario es ENCARGADO, puede crear animales
        if (rolUsuario == "ENCARGADO") {

            // Muestra el botón
            binding.btnCrearAnimal.visibility = View.VISIBLE

            // Acción al pulsar el botón
            binding.btnCrearAnimal.setOnClickListener {
                val intent = Intent(this, CrearAnimalActivity::class.java)
                startActivity(intent)
            }
        } else {
            // Oculta el botón si no tiene permisos
            binding.btnCrearAnimal.visibility = View.GONE
        }

        // Carga la lista de animales desde la API
        cargarAnimales()
    }

    // Se ejecuta cada vez que la pantalla vuelve al primer plano
    override fun onResume() {
        super.onResume()

        // Recarga los animales (por si se ha creado/editado alguno)
        cargarAnimales()
    }

    // Función que obtiene los animales desde el servidor
    private fun cargarAnimales() {

        // Obtiene la API de animales usando Retrofit
        val api = RetrofitClient.getAnimalApi(this)

        // Lanza una corrutina en segundo plano (hilo IO)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Llamada a la API para obtener la lista de animales
                val lista = api.obtenerAnimales()

                // Cambia al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {

                    // Asigna el adapter al RecyclerView
                    binding.recyclerAnimales.adapter =
                        AnimalAdapter(lista) { animal: Animal ->

                            // Acción cuando se pulsa un animal de la lista
                            val intent = Intent(this@AnimalActivity, DetalleAnimalActivity::class.java)

                            // Se pasan los datos del animal a la siguiente pantalla
                            intent.putExtra("id", animal.id)
                            intent.putExtra("identificador", animal.identificador)
                            intent.putExtra("raza", animal.raza)
                            intent.putExtra("sexo", animal.sexo)
                            intent.putExtra("fecha", animal.fechaNacimiento)

                            // También se pasa el rol del usuario
                            intent.putExtra("rolUsuario", rolUsuario)

                            // Abre la pantalla de detalle
                            startActivity(intent)
                        }
                }

            } catch (e: Exception) {
                // Manejo de errores (por ahora solo imprime en consola)
                e.printStackTrace()
            }
        }
    }
}