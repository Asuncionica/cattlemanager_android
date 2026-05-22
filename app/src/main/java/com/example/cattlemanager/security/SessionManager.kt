package com.example.cattlemanager.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val appContext = context.applicationContext

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            appContext,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveSession(token: String, userId: Long, roleName: String, roleId: Long, userName: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_ROLE, roleName)
            .putLong(KEY_ROLE_ID, roleId)
            .putString(KEY_USER_NAME, userName)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, 0L)

    fun getRole(): String? = prefs.getString(KEY_ROLE, null)

    fun getRoleId(): Long = prefs.getLong(KEY_ROLE_ID, 0L)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank() && getRoleId() != 0L

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "secure_session"
        private const val KEY_TOKEN = "TOKEN"
        private const val KEY_USER_ID = "USUARIO_ID"
        private const val KEY_ROLE = "ROL"
        private const val KEY_ROLE_ID = "ROL_ID"
        private const val KEY_USER_NAME = "NOMBRE_USUARIO"
    }
}
