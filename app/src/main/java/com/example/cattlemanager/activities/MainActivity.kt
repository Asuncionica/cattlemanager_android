package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Clase que representa los datos que se envían al backend al hacer login
data class LoginRequest(val email: String, val password: String)

// Clase que representa la información del rol devuelta por el backend
data class RolResponse(val id: Long, val nombre: String)

// Clase que representa el usuario devuelto por el backend tras el login
data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: RolResponse
)

// Interfaz de Retrofit para definir el endpoint de autenticación
interface AuthApi {

    // Petición POST al endpoint "usuarios/login"
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): UsuarioResponse
}

// Activity principal de la aplicación.
// Se encarga de autenticar al usuario y enviarlo a su pantalla correspondiente.
class MainActivity : AppCompatActivity() {

    // Binding para acceder a los elementos del layout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de Retrofit para conectar con el backend
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.133:8085/") // Dirección base del servidor
            .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos Kotlin
            .build()

        // Crea la implementación de la API de autenticación
        val api = retrofit.create(AuthApi::class.java)

        // Evento al pulsar el botón de login
        binding.loginButton.setOnClickListener {

            // Recoge email y contraseña escritos por el usuario
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Comprueba que ambos campos estén rellenados
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Muestra barra de carga y desactiva el botón para evitar múltiples clics
            binding.progressBar.visibility = View.VISIBLE
            binding.loginButton.isEnabled = false

            // Lanza la operación de red en segundo plano
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Llama al backend para autenticar al usuario
                    val usuario = api.login(LoginRequest(email, password))

                    // Vuelve al hilo principal para actualizar la interfaz
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true

                        // Muestra mensaje de éxito
                        Toast.makeText(this@MainActivity, "Login correcto ✅", Toast.LENGTH_SHORT).show()

                        // Abre la pantalla correspondiente según el rol
                        abrirPantallaSegunRol(usuario)
                    }

                } catch (e: Exception) {
                    // Si ocurre un error, lo muestra en pantalla
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true

                        Toast.makeText(
                            this@MainActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    // Función que redirige al usuario a una Activity distinta según su rol
    private fun abrirPantallaSegunRol(usuario: UsuarioResponse) {

        // Elige la pantalla en función del ID del rol
        val intent = when (usuario.rol.id.toInt()) {
            1 -> Intent(this, VeterinarioActivity::class.java)
            2 -> Intent(this, EncargadoActivity::class.java)
            3 -> Intent(this, PeonActivity::class.java)
            else -> {
                Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Envía datos del usuario a la siguiente pantalla
        intent.putExtra("nombreUsuario", usuario.nombre)
        intent.putExtra("usuarioId", usuario.id)

        // Abre la Activity correspondiente
        startActivity(intent)

        // Cierra MainActivity para que no se vuelva atrás al login
        finish()
    }
}