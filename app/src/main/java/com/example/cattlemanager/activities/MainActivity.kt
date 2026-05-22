package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.databinding.ActivityMainBinding
import com.example.cattlemanager.security.SessionManager
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
    val token: String,
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
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            abrirPantallaSegunRol(sessionManager.getRoleId(), sessionManager.getUserName().orEmpty())
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.18.37:8085/")
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
                        sessionManager.saveSession(
                            token = usuario.token,
                            userId = usuario.id,
                            roleName = usuario.rol.nombre,
                            roleId = usuario.rol.id,
                            userName = usuario.nombre
                        )

                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                        Toast.makeText(this@MainActivity, "Login correcto", Toast.LENGTH_SHORT).show()
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
        abrirPantallaSegunRol(usuario.rol.id, usuario.nombre)
    }

    private fun abrirPantallaSegunRol(rolId: Long, nombreUsuario: String) {
        val intent = when (rolId.toInt()) {
            1 -> Intent(this, VeterinarioActivity::class.java)
            2 -> Intent(this, EncargadoActivity::class.java)
            3 -> Intent(this, PeonActivity::class.java)
            else -> {
                Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show()
                return
            }
        }
        intent.putExtra("nombreUsuario", nombreUsuario)
        startActivity(intent)
        finish()
    }
}
