package dev.gatopeich.leantracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gatopeich.leantracker.R
import dev.gatopeich.leantracker.data.AppDatabase
import dev.gatopeich.leantracker.data.TrackedItem
import dev.gatopeich.leantracker.domain.ContentFetcher
import dev.gatopeich.leantracker.domain.TrackedItemRepository
import kotlinx.coroutines.launch

class AddEditViewModel(private val database: AppDatabase) : ViewModel() {
    private val repository = TrackedItemRepository(database)
    private val fetcher = ContentFetcher()
    
    suspend fun getItem(id: Long): TrackedItem? = repository.getItem(id)
    
    fun saveItem(item: TrackedItem, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.insertItem(item)
            } else {
                repository.updateItem(item)
            }
            onSuccess()
        }
    }
    
    suspend fun fetchPageTitle(url: String): String {
        return try {
            val fetcher = ContentFetcher()
            val client = okhttp3.OkHttpClient()
            val request = okhttp3.Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
                .build()
            val response = client.newCall(request).execute()
            val html = response.body?.string() ?: return "Untitled"
            fetcher.extractPageTitle(html)
        } catch (e: Exception) {
            "Untitled"
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: AddEditViewModel,
    itemId: Long?,
    initialUrl: String?,
    prefilledSelector: String?,
    onBack: () -> Unit,
    onSelectElement: (String) -> Unit
) {
    var url by remember { mutableStateOf(initialUrl ?: "") }
    var name by remember { mutableStateOf("") }
    var selector by remember { mutableStateOf(prefilledSelector ?: "") }
    var regex by remember { mutableStateOf("") }
    var intervalHours by remember { mutableStateOf("24") }
    var isLoading by remember { mutableStateOf(false) }
    
    // Update selector when prefilled
    LaunchedEffect(prefilledSelector) {
        prefilledSelector?.let {
            selector = it
        }
    }
    
    LaunchedEffect(itemId) {
        if (itemId != null && itemId > 0) {
            isLoading = true
            viewModel.getItem(itemId)?.let { item ->
                url = item.url
                name = item.name
                selector = item.selector
                regex = item.regex ?: ""
                intervalHours = item.intervalHours.toString()
            }
            isLoading = false
        } else if (initialUrl != null && name.isEmpty()) {
            // Auto-generate name from page title
            isLoading = true
            val title = viewModel.fetchPageTitle(initialUrl)
            name = title
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemId == null || itemId == 0L) "Track New URL" else "Edit Tracked Item") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text(stringResource(R.string.url_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = itemId == null || itemId == 0L
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = selector,
                    onValueChange = { selector = it },
                    label = { Text(stringResource(R.string.selector_label)) },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { onSelectElement(url) },
                    enabled = url.isNotEmpty()
                ) {
                    Text(stringResource(R.string.visual_select))
                }
            }
            
            OutlinedTextField(
                value = regex,
                onValueChange = { regex = it },
                label = { Text(stringResource(R.string.regex_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = intervalHours,
                onValueChange = { intervalHours = it },
                label = { Text(stringResource(R.string.interval_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            Button(
                onClick = {
                    val item = TrackedItem(
                        id = itemId ?: 0,
                        url = url,
                        name = name.ifEmpty { url },
                        selector = selector,
                        regex = regex.ifEmpty { null },
                        intervalHours = intervalHours.toIntOrNull() ?: 24
                    )
                    viewModel.saveItem(item, onSuccess = onBack)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = url.isNotEmpty() && selector.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}
