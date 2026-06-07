package com.example.cattlemanager.eventossanitarios

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityEventosSanitariosBinding
import com.example.cattlemanager.model.Animal
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventoSanitarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosSanitariosBinding
    private var listaAnimales: List<Animal> = emptyList()
    private var selectedAnimalId: Long = -1
    private var selectedAnimalNombre: String = ""
    private var selectedTipo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosSanitariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        cargarAnimales()

        binding.cardSelectorAnimal.setOnClickListener { view ->
            if (listaAnimales.isEmpty()) {
                Toast.makeText(this, "Cargando animales…", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val menu = PopupMenu(this, view)
            listaAnimales.forEachIndexed { index, animal ->
                menu.menu.add(0, index, index, animal.identificador ?: "Sin identificador")
            }
            menu.setOnMenuItemClickListener { item ->
                val animal = listaAnimales[item.itemId]
                selectedAnimalId = animal.id
                selectedAnimalNombre = animal.identificador ?: "Sin identificador"
                binding.tvAnimalSeleccionado.text = selectedAnimalNombre
                true
            }
            menu.show()
        }

        binding.cardSelectorTipo.setOnClickListener { view ->
            val tipos = listOf("Vacuna", "Desparasitación", "Tratamiento", "Revisión")
            val menu = PopupMenu(this, view)
            tipos.forEachIndexed { index, tipo -> menu.menu.add(0, index, index, tipo) }
            menu.setOnMenuItemClickListener { item ->
                selectedTipo = tipos[item.itemId]
                binding.tvTipoSeleccionado.text = selectedTipo
                true
            }
            menu.show()
        }

        binding.cardVerEventos.setOnClickListener {
            if (!validarSeleccion()) return@setOnClickListener
            val intent = Intent(this, EventosSanitariosPorTipoActivity::class.java)
            intent.putExtra("tipo", selectedTipo)
            intent.putExtra("animalId", selectedAnimalId)
            intent.putExtra("animalNombre", selectedAnimalNombre)
            startActivity(intent)
        }

        binding.cardCrearEvento.setOnClickListener {
            if (!validarSeleccion()) return@setOnClickListener
            val intent = Intent(this, CrearEventoSanitarioActivity::class.java)
            intent.putExtra("tipo", selectedTipo)
            intent.putExtra("animalId", selectedAnimalId)
            startActivity(intent)
        }
    }

    private fun validarSeleccion(): Boolean {
        if (selectedAnimalId == -1L) {
            Toast.makeText(this, "Selecciona un animal primero", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedTipo.isEmpty()) {
            Toast.makeText(this, "Selecciona el tipo de evento", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun cargarAnimales() {
        val api = RetrofitClient.getAnimalApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animales = api.obtenerAnimales()
                withContext(Dispatchers.Main) {
                    listaAnimales = animales
                    if (animales.isEmpty()) {
                        binding.tvAnimalSeleccionado.text = "Sin animales registrados"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EventoSanitarioActivity, "Error al cargar animales", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
