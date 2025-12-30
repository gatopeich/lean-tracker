package dev.gatopeich.leantracker.data

import androidx.room.*

@Dao
interface AuthCookieDao {
    @Query("SELECT * FROM auth_cookies WHERE domain = :domain")
    suspend fun getByDomain(domain: String): AuthCookie?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cookie: AuthCookie)
    
    @Delete
    suspend fun delete(cookie: AuthCookie)
    
    @Query("DELETE FROM auth_cookies")
    suspend fun deleteAll()
}
