package dev.gatopeich.leantracker.ui

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.gatopeich.leantracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementSelectorScreen(
    url: String,
    onElementSelected: (String, String) -> Unit, // selector, text
    onBack: () -> Unit
) {
    var selectedSelector by remember { mutableStateOf<String?>(null) }
    var selectedText by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_element)) },
                actions = {
                    if (selectedSelector != null) {
                        IconButton(onClick = {
                            selectedSelector?.let { selector ->
                                onElementSelected(selector, selectedText)
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = stringResource(R.string.confirm_selection))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (selectedSelector != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.selector_found, selectedSelector ?: ""),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (selectedText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Preview: ${selectedText.take(100)}${if (selectedText.length > 100) "..." else ""}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean = false
                            
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Inject JavaScript for element selection
                                view?.evaluateJavascript(ELEMENT_SELECTOR_JS) { }
                            }
                        }
                        
                        // Add JavaScript interface to receive selected element info
                        addJavascriptInterface(object {
                            @android.webkit.JavascriptInterface
                            fun onElementSelected(selector: String, text: String) {
                                selectedSelector = selector
                                selectedText = text
                            }
                        }, "Android")
                        
                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private const val ELEMENT_SELECTOR_JS = """
(function() {
    let selectedElement = null;
    
    // Generate CSS selector for an element
    function getCssSelector(el) {
        if (el.id) return '#' + el.id;
        
        let path = [];
        while (el.parentElement) {
            let selector = el.tagName.toLowerCase();
            if (el.className) {
                const classes = el.className.trim().split(/\s+/).filter(c => c);
                if (classes.length > 0) {
                    selector += '.' + classes.join('.');
                }
            }
            path.unshift(selector);
            el = el.parentElement;
            if (path.length > 3) break; // Limit depth
        }
        return path.join(' > ');
    }
    
    // Highlight element
    function highlightElement(el) {
        if (selectedElement) {
            selectedElement.style.outline = '';
        }
        selectedElement = el;
        el.style.outline = '3px solid #FF6200EE';
    }
    
    // Handle click
    document.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        
        const el = e.target;
        highlightElement(el);
        
        const selector = getCssSelector(el);
        const text = el.innerText || el.textContent || '';
        
        Android.onElementSelected(selector, text.trim());
        
        return false;
    }, true);
    
    // Show instruction
    const instruction = document.createElement('div');
    instruction.style.cssText = 'position: fixed; top: 0; left: 0; right: 0; background: #FF6200EE; color: white; padding: 10px; text-align: center; z-index: 10000; font-size: 14px;';
    instruction.textContent = 'Tap on an element to select it';
    document.body.insertBefore(instruction, document.body.firstChild);
})();
"""
