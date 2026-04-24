package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.cattlemanager.databinding.ActivityDetalleAnimalBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

        binding.btnVolver.setOnClickListener { finish() }
        configurarPermisos()
        configurarBotones()
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
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
        binding.btnBorrar.setOnClickListener { confirmarBorrado() }
    }

    private fun cargarDatos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animalApi = RetrofitClient.getAnimalApi(this@DetalleAnimalActivity)
                val sanitarioApi = RetrofitClient.getEventoSanitarioApi(this@DetalleAnimalActivity)
                val reproductivosApi = RetrofitClient.getEventoReproductivoApi(this@DetalleAnimalActivity)

                val animalDeferred = async { animalApi.obtenerAnimalPorId(animalId) }
                val sanitariosDeferred = async { sanitarioApi.obtenerEventos() }
                val reproductivosDeferred = async { reproductivosApi.obtenerEventos() }

                val animal = animalDeferred.await()
                val sanitarios = sanitariosDeferred.await().filter { it.animal?.id == animalId }
                val reproductivos = reproductivosDeferred.await().filter { it.animal?.id == animalId }

                withContext(Dispatchers.Main) {
                    mostrarAnimal(animal)
                    mostrarEventosSanitarios(sanitarios.map { "${it.tipo} — ${it.fecha}" })
                    mostrarEventosReproductivos(reproductivos.map { "${it.tipo} — ${it.fecha}" })
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleAnimalActivity, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarAnimal(animal: Animal) {
        binding.tvIdentificador.text = animal.identificador
        binding.tvRaza.text = "Raza: ${animal.raza}"
        binding.tvSexo.text = "Sexo: ${animal.sexo}"
        binding.tvFecha.text = "Nacimiento: ${animal.fechaNacimiento}"
        binding.tvGranja.text = "Granja: ${animal.granja?.nombre ?: "Sin granja"}"
    }

    private fun mostrarEventosSanitarios(eventos: List<String>) {
        binding.layoutEventosSanitarios.removeAllViews()
        if (eventos.isEmpty()) {
            binding.layoutEventosSanitarios.addView(crearTextoVacio("Sin eventos sanitarios registrados"))
            return
        }
        eventos.forEach { binding.layoutEventosSanitarios.addView(crearTarjetaEvento(it)) }
    }

    private fun mostrarEventosReproductivos(eventos: List<String>) {
        binding.layoutEventosReproductivos.removeAllViews()
        if (eventos.isEmpty()) {
            binding.layoutEventosReproductivos.addView(crearTextoVacio("Sin eventos reproductivos registrados"))
            return
        }
        eventos.forEach { binding.layoutEventosReproductivos.addView(crearTarjetaEvento(it)) }
    }

    private fun crearTarjetaEvento(texto: String): CardView {
        val card = CardView(this).apply {
            radius = 8f
            cardElevation = 2f
            setCardBackgroundColor(android.graphics.Color.WHITE)
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }
        val tv = TextView(this).apply {
            text = texto
            textSize = 14f
            setPadding(32, 20, 32, 20)
            setTextColor(resources.getColor(com.example.cattlemanager.R.color.text_primary, theme))
        }
        card.addView(tv)
        return card
    }

    private fun crearTextoVacio(texto: String): TextView {
        return TextView(this).apply {
            text = texto
            textSize = 13f
            setTextColor(resources.getColor(com.example.cattlemanager.R.color.text_hint, theme))
            setPadding(8, 8, 8, 16)
        }
    }

    private fun confirmarBorrado() {
        AlertDialog.Builder(this)
            .setTitle("Borrar animal")
            .setMessage("¿Seguro que quieres borrar este animal?")
            .setPositiveButton("Sí") { _, _ -> borrarAnimal() }
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
