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

class AnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimalesBinding
    private var rolUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rolUsuario = intent.getStringExtra("rolUsuario") ?: ""

        binding.recyclerAnimales.layoutManager = LinearLayoutManager(this)

        if (rolUsuario == "ENCARGADO") {
            binding.btnCrearAnimal.visibility = View.VISIBLE
            binding.btnCrearAnimal.setOnClickListener {
                val intent = Intent(this, CrearAnimalActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.btnCrearAnimal.visibility = View.GONE
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
                    binding.recyclerAnimales.adapter = AnimalAdapter(lista) { animal: Animal ->
                        val intent = Intent(this@AnimalActivity, DetalleAnimalActivity::class.java)
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
                e.printStackTrace()
            }
        }
    }
}