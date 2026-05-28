package com.example.cattlemanager.granja

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cattlemanager.databinding.ActivityEditarGranjaBinding
import com.example.cattlemanager.model.GranjaRequest
import com.example.cattlemanager.network.RetrofitClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarGranjaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarGranjaBinding
    private var id: Long = 0

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) obtenerUbicacionActual()
        else Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarGranjaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getLongExtra("id", 0)
        binding.btnVolver.setOnClickListener { finish() }
        binding.tvTituloFormularioGranja.text = "Editar Granja"
        binding.btnGuardar.text = "Actualizar"

        binding.etNombre.setText(intent.getStringExtra("nombre"))
        binding.etUbicacion.setText(intent.getStringExtra("ubicacion"))
        binding.etTelefono.setText(intent.getStringExtra("telefono"))

        val lat = intent.getDoubleExtra("latitude", Double.NaN)
        val lng = intent.getDoubleExtra("longitude", Double.NaN)
        if (!lat.isNaN()) binding.etLatitud.setText("%.6f".format(lat))
        if (!lng.isNaN()) binding.etLongitud.setText("%.6f".format(lng))

        binding.btnUsarUbicacion.setOnClickListener { pedirUbicacion() }
        binding.btnGuardar.setOnClickListener { actualizar() }
    }

    private fun pedirUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            obtenerUbicacionActual()
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun obtenerUbicacionActual() {
        val client = LocationServices.getFusedLocationProviderClient(this)
        try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        binding.etLatitud.setText("%.6f".format(location.latitude))
                        binding.etLongitud.setText("%.6f".format(location.longitude))
                        Toast.makeText(this, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizar() {
        val request = GranjaRequest(
            id = id,
            nombre = binding.etNombre.text.toString().trim(),
            ubicacion = binding.etUbicacion.text.toString().trim(),
            telefono = binding.etTelefono.text.toString().trim(),
            latitude = binding.etLatitud.text.toString().toDoubleOrNull(),
            longitude = binding.etLongitud.text.toString().toDoubleOrNull()
        )
        val api = RetrofitClient.getGranjaApi(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.actualizarGranja(id, request)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarGranjaActivity, "Granja actualizada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarGranjaActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
