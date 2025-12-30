package dev.gatopeich.leantracker.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gatopeich.leantracker.R
import dev.gatopeich.leantracker.data.AppDatabase
import dev.gatopeich.leantracker.data.TrackedItem
import dev.gatopeich.leantracker.domain.TrackedItemRepository
import dev.gatopeich.leantracker.util.ContentDiffer
import kotlinx.coroutines.launch

class DetailViewModel(private val database: AppDatabase) : ViewModel() {
    private val repository = TrackedItemRepository(database)
    
    suspend fun getItem(id: Long): TrackedItem? = repository.getItem(id)
    
    fun clearChange(id: Long) {
        viewModelScope.launch {
            repository.clearChange(id)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    itemId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    var item by remember { mutableStateOf<TrackedItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(itemId) {
        isLoading = true
        item = viewModel.getItem(itemId)
        isLoading = false
        
        // Clear change flag when viewing
        item?.let {
            if (it.hasChange) {
                viewModel.clearChange(itemId)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { onEdit(itemId) }) {
                        Text("Edit")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Item not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("URL", style = MaterialTheme.typography.labelMedium)
                        Text(item!!.url, style = MaterialTheme.typography.bodyMedium)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Selector", style = MaterialTheme.typography.labelMedium)
                        Text(item!!.selector, style = MaterialTheme.typography.bodyMedium)
                        
                        if (item!!.regex != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Regex", style = MaterialTheme.typography.labelMedium)
                            Text(item!!.regex!!, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                
                if (item!!.lastContent != null) {
                    Text(
                        text = if (item!!.hasChange) "Content Diff" else "Current Content",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Card {
                        val diff = ContentDiffer.createDiff(
                            if (item!!.hasChange) null else item!!.lastContent,
                            item!!.lastContent!!
                        )
                        
                        Text(
                            text = diff,
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(16.dp),
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
