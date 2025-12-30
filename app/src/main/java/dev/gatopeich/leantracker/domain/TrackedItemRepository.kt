package dev.gatopeich.leantracker.domain

import dev.gatopeich.leantracker.data.AppDatabase
import dev.gatopeich.leantracker.data.TrackedItem
import kotlinx.coroutines.flow.Flow

class TrackedItemRepository(private val database: AppDatabase) {
    fun getAllItems(): Flow<List<TrackedItem>> = database.trackedItemDao().getAllFlow()
    
    suspend fun getItem(id: Long): TrackedItem? = database.trackedItemDao().getById(id)
    
    suspend fun getAllItemsList(): List<TrackedItem> = database.trackedItemDao().getAll()
    
    suspend fun insertItem(item: TrackedItem): Long = database.trackedItemDao().insert(item)
    
    suspend fun updateItem(item: TrackedItem) = database.trackedItemDao().update(item)
    
    suspend fun deleteItem(item: TrackedItem) = database.trackedItemDao().delete(item)
    
    suspend fun clearChange(id: Long) = database.trackedItemDao().clearChange(id)
}
