package com.example.cattlemanager.activities

import android.os.Bundle
import android.graphics.Color
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanager.adapter.LoteGeneticoAdapter
import com.example.cattlemanager.databinding.ActivityLotesGeneticosBinding
import com.example.cattlemanager.model.LoteGeneticoResponse
import com.example.cattlemanager.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LotesGeneticosActivity : AppCompatActivity() {

    // 1. Declaramos el View Binding para acceder a las vistas del XML
    private lateinit var binding: ActivityLotesGeneticosBinding

    // 2. Creamos una lista en memoria para almacenar los lotes provisionalmente
    private var listaLotes: MutableList<LoteGeneticoResponse> = mutableListOf()
    private lateinit var loteAdapter: LoteGeneticoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. Inflamos el diseño usando el Binding corregido
        binding = ActivityLotesGeneticosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 4. Inicializamos los componentes básicos de la pantalla
        setupRecyclerView()
        obtenerLotesDelServidor()

        // Escucha el click del botón flotante para desplegar la ventana emergente
        binding.fabAddLote.setOnClickListener {
            mostrarDialogoCrearLote()
        }
    }

    private fun setupRecyclerView() {
        // Configuramos el RecyclerView para que muestre los elementos en una lista vertical ordenada
        loteAdapter = LoteGeneticoAdapter(listaLotes) { lote ->
            confirmarEliminacion(lote)
        }
        binding.rvLotes.layoutManager = LinearLayoutManager(this)
        binding.rvLotes.adapter = loteAdapter
    }

    private fun obtenerLotesDelServidor() {
        // Obtenemos la API de lotes desde tu cliente Retrofit
        val loteApi = RetrofitClient.getLoteGeneticoApi(this)

        // Lanzamos la petición en un hilo secundario (IO) para no congelar la pantalla
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Hacemos la llamada real a /api/lotes-geneticos
                val lotesBackend = loteApi.getLotesGeneticos()

                // Volvemos al hilo principal (Main) para actualizar la interfaz
                withContext(Dispatchers.Main) {
                    listaLotes = lotesBackend.toMutableList()
                    loteAdapter.actualizar(listaLotes)

                    // Un aviso temporal para comprobar en pantalla que los datos llegaron
                    Toast.makeText(
                        this@LotesGeneticosActivity,
                        "Lotes cargados: ${listaLotes.size}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                // Si el servidor está apagado o falla la red, entra aquí
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LotesGeneticosActivity,
                        "Error al conectar con el servidor: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun mostrarDialogoCrearLote() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nuevo Lote Genético")

        // 1. Creamos un contenedor vertical para meter los campos de texto
        val contenedorCampos = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 30, 60, 30) // Margen interno para que no pegue a los bordes
        }

        // 2. Campo para el Nombre
        val inputNombre = EditText(this).apply {
            hint = "Nombre (Ej: Holando 2026)"
            setSingleLine(true)
            setTextColor(Color.BLACK)
            setHintTextColor(Color.DKGRAY)
        }

        // 3. Campo para la Descripción
        val inputVariedad = EditText(this).apply {
            hint = "Variedad (Ej: Holando)"
            setSingleLine(true)
            setTextColor(Color.BLACK)
            setHintTextColor(Color.DKGRAY)
        }

        val inputDesc = EditText(this).apply {
            hint = "Descripción (Ej: Inseminación primavera)"
            setTextColor(Color.BLACK)
            setHintTextColor(Color.DKGRAY)
        }

        // 4. Metemos los campos dentro del contenedor y el contenedor al diálogo
        contenedorCampos.addView(inputNombre)
        contenedorCampos.addView(inputVariedad)
        contenedorCampos.addView(inputDesc)
        builder.setView(contenedorCampos)

        // 5. Configurar botón de Confirmar
        builder.setPositiveButton("Guardar") { dialog, _ ->
            val nombre = inputNombre.text.toString().trim()
            val variedad = inputVariedad.text.toString().trim()
            val descripcion = inputDesc.text.toString().trim()

            if (nombre.isNotEmpty()) {
                // CORREGIDO: Ahora llama al método real de persistencia remota
                guardarLoteEnServidor(nombre, variedad, descripcion)
            } else {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        // 6. Configurar botón de Cancelar
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        // 7. Lanzar el diálogo en pantalla
        builder.show()
    }

    private fun guardarLoteEnServidor(nombre: String, variedad: String, descripcion: String) {
        val loteApi = RetrofitClient.getLoteGeneticoApi(this)

        val nuevoLote = LoteGeneticoResponse(
            nombre = nombre,
            variedad = variedad.ifBlank { null },
            descripcion = descripcion,
            fechaCreacion = null
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loteGuardado = loteApi.crearLoteGenetico(nuevoLote)

                withContext(Dispatchers.Main) {
                    listaLotes.add(loteGuardado)
                    loteAdapter.actualizar(listaLotes)
                    Toast.makeText(
                        this@LotesGeneticosActivity,
                        "Lote '${loteGuardado.nombre}' creado con éxito",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val mensaje = if (e is HttpException && e.code() == 409) {
                        "Ya existe un lote con ese nombre"
                    } else {
                        "Fallo al guardar: ${e.message}"
                    }
                    Toast.makeText(
                        this@LotesGeneticosActivity,
                        mensaje,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun confirmarEliminacion(lote: LoteGeneticoResponse) {
        val loteId = lote.id
        if (loteId == null) {
            Toast.makeText(this, "No se puede eliminar un lote sin ID", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Eliminar lote")
            .setMessage("¿Seguro que quieres eliminar '${lote.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarLote(lote) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarLote(lote: LoteGeneticoResponse) {
        val loteId = lote.id ?: return
        val loteApi = RetrofitClient.getLoteGeneticoApi(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Ejecutamos la llamada web que ahora retorna un objeto Response
                val respuesta = loteApi.eliminarLoteGenetico(loteId)

                withContext(Dispatchers.Main) {
                    // Verificamos si la respuesta del backend indica éxito (ej: código 200 o 204)
                    if (respuesta.isSuccessful) {
                        listaLotes.removeAll { it.id == loteId }
                        loteAdapter.actualizar(listaLotes)
                        Toast.makeText(
                            this@LotesGeneticosActivity,
                            "Lote eliminado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // En caso de que falle por lógica del servidor (ej: código 409 si tiene animales)
                        val mensajeError = if (respuesta.code() == 409) {
                            "No se puede eliminar: tiene animales asociados"
                        } else {
                            "Error del servidor: ${respuesta.code()}"
                        }
                        Toast.makeText(this@LotesGeneticosActivity, mensajeError, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val mensaje = when {
                        e is HttpException && e.code() == 409 -> {
                            "No se puede eliminar: tiene animales asociados"
                        }
                        e is HttpException && e.code() == 404 -> {
                            listaLotes.removeAll { it.id == loteId }
                            loteAdapter.actualizar(listaLotes)
                            obtenerLotesDelServidor()
                            "Ese lote ya no existe. Lista actualizada."
                        }
                        else -> {
                            "Fallo al eliminar: ${e.message}"
                        }
                    }
                    Toast.makeText(
                        this@LotesGeneticosActivity,
                        mensaje,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}