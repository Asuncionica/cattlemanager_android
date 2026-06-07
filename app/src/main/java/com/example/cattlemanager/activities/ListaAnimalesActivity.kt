package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.adapter.AnimalAdapter
import com.example.cattlemanager.databinding.ActivityListaAnimalesBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaAnimalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaAnimalesBinding
    private var rolUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaAnimalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rolUsuario = intent.getStringExtra("rolUsuario") ?: ""

        binding.btnVolver.setOnClickListener { finish() }
        binding.recyclerAnimales.layoutManager = LinearLayoutManager(this)

        if (rolUsuario == "ENCARGADO") {
            binding.btnCrearAnimal.visibility = View.VISIBLE
            binding.btnCrearAnimal.setOnClickListener {
                startActivity(Intent(this, CrearAnimalActivity::class.java))
            }
        }

        cargarAnimales()
    }

    override fun onResume() {
        super.onResume()
        cargarAnimales()
    }

    private fun cargarAnimales() {
        val api = RetrofitClient.getAnimalApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerAnimales()
                withContext(Dispatchers.Main) {
                    val n = lista.size
                    binding.tvContadorAnimales.text =
                        "$n ${if (n == 1) "animal registrado" else "animales registrados"}"

                    binding.recyclerAnimales.adapter = AnimalAdapter(lista) { animal: Animal ->
                        val intent = Intent(this@ListaAnimalesActivity, DetalleAnimalActivity::class.java)
                        intent.putExtra("id", animal.id)
                        intent.putExtra("identificador", animal.identificador)
                        intent.putExtra("raza", animal.raza)
                        intent.putExtra("sexo", animal.sexo)
                        intent.putExtra("fecha", animal.fechaNacimiento)
                        intent.putExtra("rolUsuario", rolUsuario)
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR_LISTA_ANIMALES", "Error al cargar animales", e)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ListaAnimalesActivity,
                        "Error animales: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
