package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityDetalleAnimalBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Activity que muestra el detalle de un animal y permite editarlo o borrarlo
class DetalleAnimalActivity : AppCompatActivity() {

    // Binding para acceder a las vistas
    private lateinit var binding: ActivityDetalleAnimalBinding

    // ID del animal que se va a mostrar
    private var animalId: Long = 0

    // Rol del usuario (ENCARGADO, PEON, etc.)
    private var rolUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding
        binding = ActivityDetalleAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recoge datos enviados desde la Activity anterior
        animalId = intent.getLongExtra("id", 0)
        rolUsuario = intent.getStringExtra("rolUsuario") ?: ""

        // Mensaje de debug para comprobar el rol recibido
        Toast.makeText(this, "Rol recibido: $rolUsuario", Toast.LENGTH_LONG).show()

        // Configura visibilidad de botones según permisos
        configurarPermisos()

        // Configura acciones de los botones
        configurarBotones()
    }

    override fun onResume() {
        super.onResume()

        // Recarga el animal cada vez que se vuelve a la pantalla
        cargarAnimal()
    }

    // Muestra u oculta botones según el rol del usuario
    private fun configurarPermisos() {
        if (rolUsuario == "ENCARGADO") {
            binding.btnEditar.visibility = View.VISIBLE
            binding.btnBorrar.visibility = View.VISIBLE
        } else {
            binding.btnEditar.visibility = View.GONE
            binding.btnBorrar.visibility = View.GONE
        }
    }

    // Define qué hacen los botones
    private fun configurarBotones() {

        // Botón editar
        binding.btnEditar.setOnClickListener {

            // Abre la pantalla de edición pasando los datos actuales
            val intent = Intent(this, EditarAnimalActivity::class.java)
            intent.putExtra("id", animalId)
            intent.putExtra("identificador", binding.tvIdentificador.text.toString())

            // Se eliminan los textos añadidos ("Raza: ", etc.)
            intent.putExtra("raza", binding.tvRaza.text.toString().replace("Raza: ", ""))
            intent.putExtra("sexo", binding.tvSexo.text.toString().replace("Sexo: ", ""))
            intent.putExtra("fecha", binding.tvFecha.text.toString().replace("Nacimiento: ", ""))

            startActivity(intent)
        }

        // Botón borrar
        binding.btnBorrar.setOnClickListener {
            confirmarBorrado()
        }
    }

    // Llama a la API para obtener el animal por ID
    private fun cargarAnimal() {
        val api = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animal = api.obtenerAnimalPorId(animalId)

                // Actualiza la UI en el hilo principal
                withContext(Dispatchers.Main) {
                    mostrarAnimal(animal)
                }
            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetalleAnimalActivity,
                        "Error al cargar detalle del animal",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Muestra los datos del animal en pantalla
    private fun mostrarAnimal(animal: Animal) {
        binding.tvIdentificador.text = animal.identificador
        binding.tvRaza.text = "Raza: ${animal.raza}"
        binding.tvSexo.text = "Sexo: ${animal.sexo}"
        binding.tvFecha.text = "Nacimiento: ${animal.fechaNacimiento}"

        // Si el animal tiene granja, se muestra, si no, se indica
        val nombreGranja = animal.granja?.nombre ?: "Sin granja"
        binding.tvGranja.text = "Granja: $nombreGranja"
    }

    // Muestra un diálogo de confirmación antes de borrar
    private fun confirmarBorrado() {
        AlertDialog.Builder(this)
            .setTitle("Borrar animal")
            .setMessage("¿Seguro que quieres borrar este animal?")
            .setPositiveButton("Sí") { _, _ ->
                borrarAnimal()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Llama a la API para borrar el animal
    private fun borrarAnimal() {
        val api = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.borrarAnimal(animalId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleAnimalActivity, "Animal borrado", Toast.LENGTH_SHORT).show()

                    // Cierra la pantalla y vuelve atrás
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleAnimalActivity, "Error al borrar animal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}