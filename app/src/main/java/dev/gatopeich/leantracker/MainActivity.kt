package dev.gatopeich.leantracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.gatopeich.leantracker.data.AppDatabase
import dev.gatopeich.leantracker.ui.*
import dev.gatopeich.leantracker.ui.theme.LeanTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedUrl = extractSharedUrl(intent)
        val itemId = intent.getLongExtra("item_id", -1L)
        
        setContent {
            LeanTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LeanTrackerApp(
                        sharedUrl = sharedUrl,
                        notificationItemId = if (itemId > 0) itemId else null
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            setIntent(it)
            // Restart activity to handle new intent
            recreate()
        }
    }
    
    private fun extractSharedUrl(intent: Intent?): String? {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            return intent.getStringExtra(Intent.EXTRA_TEXT)
                ?.let { text ->
                    // Extract URL from text (might contain additional text)
                    Regex("https?://[^\\s]+").find(text)?.value
                }
        }
        return null
    }
}

@Composable
fun LeanTrackerApp(
    sharedUrl: String? = null,
    notificationItemId: Long? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember(context) { AppDatabase.getDatabase(context) }
    val mainViewModel: MainViewModel = viewModel { MainViewModel(database) }
    val addEditViewModel: AddEditViewModel = viewModel { AddEditViewModel(database) }
    val detailViewModel: DetailViewModel = viewModel { DetailViewModel(database) }
    
    val trackedItems by mainViewModel.trackedItems.collectAsState()
    
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    var pendingSelector by remember { mutableStateOf<PendingSelector?>(null) }
    
    // Handle shared URL
    LaunchedEffect(sharedUrl) {
        if (sharedUrl != null) {
            currentScreen = Screen.AddEdit(null, sharedUrl, null)
        }
    }
    
    // Handle notification tap
    LaunchedEffect(notificationItemId) {
        if (notificationItemId != null && notificationItemId > 0) {
            currentScreen = Screen.Detail(notificationItemId)
        }
    }
    
    when (val screen = currentScreen) {
        is Screen.Main -> {
            MainScreen(
                trackedItems = trackedItems,
                onAddClick = { currentScreen = Screen.AddEdit(null, null, null) },
                onItemClick = { item -> currentScreen = Screen.Detail(item.id) },
                onDeleteItem = { item -> mainViewModel.deleteItem(item) }
            )
        }
        is Screen.AddEdit -> {
            AddEditScreen(
                viewModel = addEditViewModel,
                itemId = screen.itemId,
                initialUrl = screen.initialUrl,
                prefilledSelector = screen.prefilledSelector ?: pendingSelector?.selector,
                onBack = { 
                    pendingSelector = null
                    currentScreen = Screen.Main 
                },
                onSelectElement = { url ->
                    currentScreen = Screen.ElementSelector(url) { selector, _ ->
                        pendingSelector = PendingSelector(selector)
                        currentScreen = Screen.AddEdit(screen.itemId, screen.initialUrl, selector)
                    }
                }
            )
        }
        is Screen.Detail -> {
            DetailScreen(
                viewModel = detailViewModel,
                itemId = screen.itemId,
                onBack = { currentScreen = Screen.Main },
                onEdit = { id -> currentScreen = Screen.AddEdit(id, null, null) }
            )
        }
        is Screen.ElementSelector -> {
            ElementSelectorScreen(
                url = screen.url,
                onElementSelected = { selector, text ->
                    screen.onSelected(selector, text)
                },
                onBack = { 
                    pendingSelector = null
                    currentScreen = Screen.Main 
                }
            )
        }
    }
}

data class PendingSelector(val selector: String)

sealed class Screen {
    object Main : Screen()
    data class AddEdit(val itemId: Long?, val initialUrl: String?, val prefilledSelector: String?) : Screen()
    data class Detail(val itemId: Long) : Screen()
    data class ElementSelector(
        val url: String,
        val onSelected: (String, String) -> Unit
    ) : Screen()
}
