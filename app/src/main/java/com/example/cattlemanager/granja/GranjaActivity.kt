package com.example.cattlemanager.granja
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityGranjaBinding
import com.example.cattlemanager.model.Granja
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class GranjaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGranjaBinding
    private var granjaActual: Granja? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGranjaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnVolver.setOnClickListener { finish() }
        binding.btnEditarGranja.setOnClickListener {
            granjaActual?.let {
                val intent = Intent(this, EditarGranjaActivity::class.java)
                intent.putExtra("id", it.id)
                intent.putExtra("nombre", it.nombre)
                intent.putExtra("ubicacion", it.ubicacion)
                intent.putExtra("telefono", it.telefono)
                startActivity(intent)
            }
        }
        binding.btnCrearGranja.setOnClickListener {
            startActivity(Intent(this, CrearGranjaActivity::class.java))
        }
        cargarGranja()
    }
    override fun onResume() {
        super.onResume()
        cargarGranja()
    }
    private fun cargarGranja() {
        val api = RetrofitClient.getGranjaApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = api.obtenerGranjas()
                val granja = lista.firstOrNull()
                granjaActual = granja
                withContext(Dispatchers.Main) {
                    if (granja != null) {
                        mostrarGranja(granja)
                    } else {
                        mostrarSinGranja()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GranjaActivity, "Error al cargar la granja", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun mostrarGranja(granja: Granja) {
        binding.cardGranja.visibility = View.VISIBLE
        binding.btnEditarGranja.visibility = View.VISIBLE
        binding.tvMensajeSinGranja.visibility = View.GONE
        binding.btnCrearGranja.visibility = View.GONE
        binding.tvNombreGranja.text = granja.nombre
        binding.tvUbicacion.text = "Ubicación: ${granja.ubicacion}"
        binding.tvTelefono.text = "Teléfono: ${granja.telefono}"
    }
    private fun mostrarSinGranja() {
        binding.cardGranja.visibility = View.GONE
        binding.btnEditarGranja.visibility = View.GONE
        binding.tvMensajeSinGranja.visibility = View.VISIBLE
        binding.btnCrearGranja.visibility = View.VISIBLE
    }
}