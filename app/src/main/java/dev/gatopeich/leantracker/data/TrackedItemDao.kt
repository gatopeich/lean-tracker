package dev.gatopeich.leantracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedItemDao {
    @Query("SELECT * FROM tracked_items ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<TrackedItem>>
    
    @Query("SELECT * FROM tracked_items")
    suspend fun getAll(): List<TrackedItem>
    
    @Query("SELECT * FROM tracked_items WHERE id = :id")
    suspend fun getById(id: Long): TrackedItem?
    
    @Insert
    suspend fun insert(item: TrackedItem): Long
    
    @Update
    suspend fun update(item: TrackedItem)
    
    @Delete
    suspend fun delete(item: TrackedItem)
    
    @Query("UPDATE tracked_items SET hasChange = 0 WHERE id = :id")
    suspend fun clearChange(id: Long)
}
