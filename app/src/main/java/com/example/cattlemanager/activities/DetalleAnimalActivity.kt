package com.example.cattlemanager.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.R
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
    private var esMacho: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animalId = intent.getLongExtra("id", 0)
        rolUsuario = intent.getStringExtra("rolUsuario") ?: ""

        val identificador = intent.getStringExtra("identificador") ?: ""
        val raza = intent.getStringExtra("raza") ?: ""
        val sexo = intent.getStringExtra("sexo") ?: ""
        esMacho = sexo.equals("Macho", ignoreCase = true)

        binding.tvIdentificador.text = identificador
        binding.tvSubtituloHeader.text = raza
        aplicarEstiloSexo()

        binding.btnVolver.setOnClickListener { finish() }
        configurarPermisos()
        configurarBotones()
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun aplicarEstiloSexo() {
        val colorSexo = if (esMacho) Color.parseColor("#1E88E5") else Color.parseColor("#60AD5E")
        binding.tvSubtituloHeader.setTextColor(colorSexo)
        binding.panelInfo.setBackgroundResource(
            if (esMacho) R.drawable.animal_card_male else R.drawable.animal_card_female
        )
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
            intent.putExtra("raza", binding.tvRaza.text.toString())
            intent.putExtra("sexo", binding.tvSexo.text.toString())
            intent.putExtra("fecha", binding.tvFecha.text.toString())
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
        esMacho = animal.sexo.equals("Macho", ignoreCase = true)
        binding.tvIdentificador.text = animal.identificador
        binding.tvSubtituloHeader.text = animal.raza
        binding.tvSexo.text = animal.sexo
        binding.tvRaza.text = animal.raza
        binding.tvFecha.text = animal.fechaNacimiento
        binding.tvGranja.text = animal.granja?.nombre ?: "Sin granja"
        binding.tvLoteGenetico.text = animal.loteGenetico?.nombre ?: "Sin lote asignado"
        aplicarEstiloSexo()
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

    private fun crearTarjetaEvento(texto: String): View {
        val borderColor = if (esMacho) "#1565C0" else "#2E7D32"
        val bgDrawable = if (esMacho) R.drawable.animal_card_male else R.drawable.animal_card_female
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(bgDrawable)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (8 * resources.displayMetrics.density).toInt()
            }
            val pad = (14 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
        }
        val tv = TextView(this).apply {
            text = texto
            textSize = 14f
            setTextColor(Color.WHITE)
        }
        container.addView(tv)
        return container
    }

    private fun crearTextoVacio(texto: String): TextView {
        return TextView(this).apply {
            text = texto
            textSize = 13f
            setTextColor(Color.parseColor("#AAFFFFFF"))
            val pad = (8 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, (16 * resources.displayMetrics.density).toInt())
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
