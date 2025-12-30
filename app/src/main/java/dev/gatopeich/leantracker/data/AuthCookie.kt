package dev.gatopeich.leantracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_cookies")
data class AuthCookie(
    @PrimaryKey
    val domain: String,
    val cookies: String, // JSON serialized cookie data
    val updatedAt: Long = System.currentTimeMillis()
)
