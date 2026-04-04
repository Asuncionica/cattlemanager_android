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

// ----- Request y Response -----
data class LoginRequest(val email: String, val password: String)

data class RolResponse(val id: Long, val nombre: String)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: RolResponse
)

// ----- Interfaz API -----
interface AuthApi {
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): UsuarioResponse
}

// ----- MainActivity -----
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.133:8085/") // Cambia por tu IP local
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(AuthApi::class.java)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.loginButton.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val usuario = api.login(LoginRequest(email, password))

                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                        Toast.makeText(this@MainActivity, "Login correcto ✅", Toast.LENGTH_SHORT).show()
                        abrirPantallaSegunRol(usuario)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun abrirPantallaSegunRol(usuario: UsuarioResponse) {
        val intent = when (usuario.rol.id.toInt()) {
            1 -> Intent(this, VeterinarioActivity::class.java)
            2 -> Intent(this, EncargadoActivity::class.java)
            3 -> Intent(this, PeonActivity::class.java)
            else -> {
                Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show()
                return
            }
        }
        intent.putExtra("nombreUsuario", usuario.nombre)
        startActivity(intent)
        finish()
    }
}
