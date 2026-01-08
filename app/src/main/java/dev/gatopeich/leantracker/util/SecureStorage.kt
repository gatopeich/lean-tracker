package dev.gatopeich.leantracker.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "encrypted_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveCredential(domain: String, username: String, password: String) {
        prefs.edit()
            .putString("${domain}_username", username)
            .putString("${domain}_password", password)
            .apply()
    }
    
    fun getCredential(domain: String): Pair<String, String>? {
        val username = prefs.getString("${domain}_username", null)
        val password = prefs.getString("${domain}_password", null)
        return if (username != null && password != null) {
            Pair(username, password)
        } else null
    }
    
    fun clearCredential(domain: String) {
        prefs.edit()
            .remove("${domain}_username")
            .remove("${domain}_password")
            .apply()
    }
}
