package com.example.cattlemanager.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanager.BuildConfig
import com.example.cattlemanager.R
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // =========================================================================
        // ◄ COMPROBACIÓN DE PERSISTENCIA (SESIÓN ACTIVA) ►
        // Si el usuario ya está logueado y el token no ha expirado, salta el login
        // =========================================================================
        if (sessionManager.isLoggedIn()) {
            val rolId = sessionManager.getRoleId()
            val nombreUsuario = sessionManager.getUserName() ?: ""

            val intent = when (rolId.toInt()) {
                1 -> Intent(this, VeterinarioActivity::class.java)
                2 -> Intent(this, EncargadoActivity::class.java)
                3 -> Intent(this, PeonActivity::class.java)
                else -> null
            }

            if (intent != null) {
                intent.putExtra("nombreUsuario", nombreUsuario)
                startActivity(intent)
                finish() // Cierra MainActivity para que no puedan regresar atrás al login
                return // Detiene el onCreate aquí para que no dibuje el formulario
            }
        }
        // =========================================================================

        var passwordVisible = false
        binding.togglePasswordButton.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                binding.passwordEditText.transformationMethod = null
                binding.togglePasswordButton.setImageResource(R.drawable.ic_visibility_off)
            } else {
                binding.passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.togglePasswordButton.setImageResource(R.drawable.ic_visibility)
            }
            binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(AuthApi::class.java)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
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
                        abrirPantallaSegunRol(usuario)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                        Toast.makeText(this@MainActivity, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
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