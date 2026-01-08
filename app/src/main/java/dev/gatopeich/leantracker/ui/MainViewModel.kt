package dev.gatopeich.leantracker.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gatopeich.leantracker.data.AppDatabase
import dev.gatopeich.leantracker.data.TrackedItem
import dev.gatopeich.leantracker.domain.TrackedItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(database: AppDatabase) : ViewModel() {
    private val repository = TrackedItemRepository(database)
    
    val trackedItems: StateFlow<List<TrackedItem>> = repository.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun deleteItem(item: TrackedItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }
    
    fun clearChange(id: Long) {
        viewModelScope.launch {
            repository.clearChange(id)
        }
    }
}
