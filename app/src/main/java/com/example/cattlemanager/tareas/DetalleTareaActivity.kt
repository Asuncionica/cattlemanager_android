package com.example.cattlemanager.tareas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityDetalleTareaBinding
import com.example.cattlemanager.model.GranjaIdRequest
import com.example.cattlemanager.model.TareaRequest
import com.example.cattlemanager.model.UsuarioIdRequest
import com.example.cattlemanager.network.RetrofitClient
import com.example.cattlemanager.util.DateFormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleTareaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleTareaBinding
    private var tareaId: Long = 0
    private var completada: Boolean = false
    private var titulo: String = ""
    private var descripcion: String = ""
    private var fechaVencimiento: String = ""
    private var granjaId: Long = 0
    private var peonId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleTareaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tareaId = intent.getLongExtra("id", 0)
        binding.btnVolver.setOnClickListener { finish() }
        titulo = intent.getStringExtra("titulo") ?: ""
        descripcion = intent.getStringExtra("descripcion") ?: ""
        fechaVencimiento = intent.getStringExtra("fechaVencimiento") ?: ""
        completada = intent.getBooleanExtra("completada", false)
        granjaId = intent.getLongExtra("granjaId", 0)
        peonId = intent.getLongExtra("peonId", 0)
        val peonNombre = intent.getStringExtra("peonNombre") ?: "Sin asignar"

        val rolUsuario = intent.getStringExtra("rolUsuario") ?: "ENCARGADO"

        binding.tvTituloDetalleTarea.text = titulo
        binding.tvFechaDetalleTarea.text = "Vencimiento: ${DateFormatUtils.toSpanishDisplay(fechaVencimiento)}"
        binding.tvDescripcionDetalleTarea.text = "Descripción: $descripcion"
        binding.tvPeonDetalleTarea.text = "Peón asignado: $peonNombre"
        actualizarTextoEstado()

        if (rolUsuario != "ENCARGADO") {
            binding.btnBorrarTarea.visibility = View.GONE
        }

        binding.btnToggleCompletada.setOnClickListener {
            toggleEstado()
        }

        binding.btnBorrarTarea.setOnClickListener {
            confirmarBorrado()
        }
    }

    private fun actualizarTextoEstado() {
        binding.tvEstadoDetalleTarea.text = if (completada) "Estado: Completada" else "Estado: Pendiente"
        binding.btnToggleCompletada.text = if (completada) "Marcar como pendiente" else "Marcar como completada"
    }

    private fun toggleEstado() {
        val nuevaCompletada = !completada

        val tarea = TareaRequest(
            titulo = titulo,
            descripcion = descripcion,
            fechaVencimiento = fechaVencimiento,
            completada = nuevaCompletada,
            granja = GranjaIdRequest(granjaId),
            peon = if (peonId != 0L) UsuarioIdRequest(peonId) else null
        )

        val api = RetrofitClient.getTareaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.actualizarTarea(tareaId, tarea)
                completada = nuevaCompletada

                withContext(Dispatchers.Main) {
                    actualizarTextoEstado()
                    val msg = if (completada) "Tarea marcada como completada" else "Tarea marcada como pendiente"
                    Toast.makeText(this@DetalleTareaActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleTareaActivity, "Error al actualizar tarea", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun confirmarBorrado() {
        AlertDialog.Builder(this)
            .setTitle("Borrar tarea")
            .setMessage("¿Seguro que quieres borrar esta tarea?")
            .setPositiveButton("Sí") { _, _ -> borrarTarea() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrarTarea() {
        val api = RetrofitClient.getTareaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.eliminarTarea(tareaId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleTareaActivity, "Tarea borrada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleTareaActivity, "Error al borrar tarea", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
