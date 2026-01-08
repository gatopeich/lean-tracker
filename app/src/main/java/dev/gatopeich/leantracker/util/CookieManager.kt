package dev.gatopeich.leantracker.util

import okhttp3.Cookie

object CookieManager {
    // Known authentication cookie patterns (session, auth tokens, etc.)
    private val authCookiePatterns = listOf(
        "session", "auth", "token", "login", "user", "account", "jwt", "sid"
    )
    
    // Known tracking cookie patterns (analytics, advertising, etc.)
    private val trackingCookiePatterns = listOf(
        "_ga", "_gid", "_fbp", "_gcl", "utm_", "tracking", "analytics"
    )
    
    fun isAuthCookie(cookie: Cookie): Boolean {
        val name = cookie.name.lowercase()
        return authCookiePatterns.any { pattern -> 
            name.contains(pattern) 
        } && !isTrackingCookie(cookie)
    }
    
    fun isTrackingCookie(cookie: Cookie): Boolean {
        val name = cookie.name.lowercase()
        return trackingCookiePatterns.any { pattern -> 
            name.contains(pattern) 
        }
    }
    
    fun filterAuthCookies(cookies: List<Cookie>): List<Cookie> {
        return cookies.filter { isAuthCookie(it) }
    }
    
    fun cookiesToString(cookies: List<Cookie>): String {
        return cookies.joinToString("; ") { "${it.name}=${it.value}" }
    }
    
    fun parseCookieString(cookieString: String): Map<String, String> {
        return cookieString.split(";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { 
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) parts[0] to parts[1] else null
            }
            .toMap()
    }
}
