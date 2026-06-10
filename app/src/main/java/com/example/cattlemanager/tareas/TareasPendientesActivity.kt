package com.example.cattlemanager.tareas

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.databinding.ActivityTareasPendientesBinding
import com.example.cattlemanager.model.Tarea
import com.example.cattlemanager.network.RetrofitClient
import com.example.cattlemanager.security.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareasPendientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTareasPendientesBinding
    private lateinit var sessionManager: SessionManager

    private var peonId: Long = 0L
    private var todasLasTareas: List<Tarea> = emptyList()
    private val urgentIds = mutableSetOf<Long>()

    private enum class Filtro { PENDIENTES, TODAS, COMPLETADAS }

    private var filtroActual = Filtro.PENDIENTES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTareasPendientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        peonId = intent.getLongExtra(
            "peonId",
            sessionManager.getUserId()
        )

        cargarUrgentes()

        binding.recyclerTareasPendientes.layoutManager = LinearLayoutManager(this)

        binding.btnVolver.setOnClickListener {
            finish()
        }

        binding.btnFiltroPendientes.setOnClickListener {
            cambiarFiltro(Filtro.PENDIENTES)
        }

        binding.btnFiltroTodas.setOnClickListener {
            cambiarFiltro(Filtro.TODAS)
        }

        binding.btnFiltroCompletadas.setOnClickListener {
            cambiarFiltro(Filtro.COMPLETADAS)
        }

        actualizarEstiloFiltros()
        cargarTareas()
    }

    override fun onResume() {
        super.onResume()
        cargarTareas()
    }

    private fun cargarUrgentes() {
        val prefs = getSharedPreferences("urgent_tasks", MODE_PRIVATE)
        val ids = prefs.getStringSet("ids", emptySet()) ?: emptySet()

        urgentIds.clear()
        urgentIds.addAll(ids.mapNotNull { it.toLongOrNull() })
    }

    private fun guardarUrgentes() {
        getSharedPreferences("urgent_tasks", MODE_PRIVATE)
            .edit()
            .putStringSet("ids", urgentIds.map { it.toString() }.toSet())
            .apply()
    }

    private fun toggleUrgente(tareaId: Long) {
        if (tareaId in urgentIds) {
            urgentIds.remove(tareaId)
        } else {
            urgentIds.add(tareaId)
        }

        guardarUrgentes()
        renderTareas()
    }

    private fun cambiarFiltro(filtro: Filtro) {
        filtroActual = filtro
        actualizarEstiloFiltros()
        renderTareas()
    }

    private fun actualizarEstiloFiltros() {
        val inactivoBg = Color.TRANSPARENT
        val inactivoBorde = Color.parseColor("#55FFFFFF")
        val inactivoTexto = Color.parseColor("#CCFFFFFF")

        data class BtnCfg(
            val color: String,
            val activo: Boolean,
            val btn: com.google.android.material.button.MaterialButton
        )

        listOf(
            BtnCfg("#FFB74D", filtroActual == Filtro.PENDIENTES, binding.btnFiltroPendientes),
            BtnCfg("#78909C", filtroActual == Filtro.TODAS, binding.btnFiltroTodas),
            BtnCfg("#81C784", filtroActual == Filtro.COMPLETADAS, binding.btnFiltroCompletadas)
        ).forEach { (color, activo, btn) ->
            val c = Color.parseColor(color)

            btn.backgroundTintList = ColorStateList.valueOf(
                if (activo) c else inactivoBg
            )

            btn.strokeColor = ColorStateList.valueOf(
                if (activo) c else inactivoBorde
            )

            btn.setTextColor(
                if (activo) Color.WHITE else inactivoTexto
            )
        }
    }

    private fun tareasFiltradas(): List<Tarea> {
        val filtradas = when (filtroActual) {
            Filtro.PENDIENTES -> todasLasTareas.filter { !it.completada }
            Filtro.TODAS -> todasLasTareas
            Filtro.COMPLETADAS -> todasLasTareas.filter { it.completada }
        }

        return filtradas.sortedWith(
            compareByDescending<Tarea> { it.id in urgentIds }
                .thenBy { it.fechaVencimiento }
        )
    }

    private fun renderTareas() {
        val lista = tareasFiltradas()

        val etiquetaContador = when (filtroActual) {
            Filtro.PENDIENTES -> "${lista.size} pendiente${if (lista.size != 1) "s" else ""}"
            Filtro.TODAS -> "${lista.size} en total"
            Filtro.COMPLETADAS -> "${lista.size} completada${if (lista.size != 1) "s" else ""}"
        }

        binding.tvContadorPendientes.text = etiquetaContador

        if (lista.isEmpty()) {
            binding.layoutSinTareas.visibility = View.VISIBLE
            binding.recyclerTareasPendientes.visibility = View.GONE
        } else {
            binding.layoutSinTareas.visibility = View.GONE
            binding.recyclerTareasPendientes.visibility = View.VISIBLE

            binding.recyclerTareasPendientes.adapter = TareaAdapter(
                lista = lista,
                onClick = { tarea -> abrirDetalle(tarea) },
                urgentIds = urgentIds.toSet(),
                onUrgentToggle = { id -> toggleUrgente(id) }
            )
        }
    }

    private fun cargarTareas() {
        if (peonId == 0L) {
            binding.tvContadorPendientes.text = "0 pendientes"
            binding.layoutSinTareas.visibility = View.VISIBLE
            binding.recyclerTareasPendientes.visibility = View.GONE

            Toast.makeText(
                this,
                "No se encontró el ID del peón",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val api = RetrofitClient.getTareaApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuesta = api.obtenerTareasPorPeon(peonId)
                val tareas = respuesta.content

                withContext(Dispatchers.Main) {
                    todasLasTareas = tareas
                    renderTareas()
                }

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@TareasPendientesActivity,
                        "Error al cargar tareas del peón",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun abrirDetalle(tarea: Tarea) {
        val intent = Intent(this, DetalleTareaActivity::class.java)

        intent.putExtra("id", tarea.id)
        intent.putExtra("titulo", tarea.titulo)
        intent.putExtra("descripcion", tarea.descripcion)
        intent.putExtra("fechaVencimiento", tarea.fechaVencimiento)
        intent.putExtra("completada", tarea.completada)
        intent.putExtra("granjaId", tarea.granja?.id ?: 0L)
        intent.putExtra("peonId", tarea.peon?.id ?: 0L)
        intent.putExtra("peonNombre", tarea.peon?.nombre ?: "")
        intent.putExtra("rolUsuario", "PEON")

        startActivity(intent)
    }
}