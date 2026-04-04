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

class DetalleAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAnimalBinding
    private var animalId: Long = 0
    private var rolUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animalId = intent.getLongExtra("id", 0)
        rolUsuario = intent.getStringExtra("rolUsuario") ?: ""

        Toast.makeText(this, "Rol recibido: $rolUsuario", Toast.LENGTH_LONG).show()

        configurarPermisos()
        configurarBotones()
    }

    override fun onResume() {
        super.onResume()
        cargarAnimal()
    }

    private fun configurarPermisos() {
        if (rolUsuario == "ENCARGADO") {
            binding.btnEditar.visibility = View.VISIBLE
            binding.btnBorrar.visibility = View.VISIBLE
        } else {
            binding.btnEditar.visibility = View.GONE
            binding.btnBorrar.visibility = View.GONE
        }
    }

    private fun configurarBotones() {
        binding.btnEditar.setOnClickListener {
            val intent = Intent(this, EditarAnimalActivity::class.java)
            intent.putExtra("id", animalId)
            intent.putExtra("identificador", binding.tvIdentificador.text.toString())
            intent.putExtra("raza", binding.tvRaza.text.toString().replace("Raza: ", ""))
            intent.putExtra("sexo", binding.tvSexo.text.toString().replace("Sexo: ", ""))
            intent.putExtra("fecha", binding.tvFecha.text.toString().replace("Nacimiento: ", ""))
            startActivity(intent)
        }

        binding.btnBorrar.setOnClickListener {
            confirmarBorrado()
        }
    }

    private fun cargarAnimal() {
        val api = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animal = api.obtenerAnimalPorId(animalId)

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

    private fun mostrarAnimal(animal: Animal) {
        binding.tvIdentificador.text = animal.identificador
        binding.tvRaza.text = "Raza: ${animal.raza}"
        binding.tvSexo.text = "Sexo: ${animal.sexo}"
        binding.tvFecha.text = "Nacimiento: ${animal.fechaNacimiento}"
        val nombreGranja = animal.granja?.nombre ?: "Sin granja"
        binding.tvGranja.text = "Granja: $nombreGranja"
    }

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

    private fun borrarAnimal() {
        val api = RetrofitClient.getAnimalApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.borrarAnimal(animalId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleAnimalActivity, "Animal borrado", Toast.LENGTH_SHORT).show()
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