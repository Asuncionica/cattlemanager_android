package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityAnimalesBinding
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

        binding.btnVolver.setOnClickListener { finish() }

        if (rolUsuario == "ENCARGADO") {
            binding.cardCrearAnimal.visibility = View.VISIBLE
        }

        binding.cardVerAnimales.setOnClickListener {
            val intent = Intent(this, ListaAnimalesActivity::class.java)
            intent.putExtra("rolUsuario", rolUsuario)
            startActivity(intent)
        }

        binding.cardCrearAnimal.setOnClickListener {
            startActivity(Intent(this, CrearAnimalActivity::class.java))
        }

        cargarContador()
    }

    override fun onResume() {
        super.onResume()
        cargarContador()
    }

    private fun cargarContador() {
        val api = RetrofitClient.getAnimalApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerAnimales()
                withContext(Dispatchers.Main) {
                    val n = lista.size
                    binding.tvContadorAnimales.text =
                        "$n ${if (n == 1) "animal registrado" else "animales registrados"}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
