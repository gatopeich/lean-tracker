package dev.gatopeich.leantracker.domain

import dev.gatopeich.leantracker.data.TrackedItem
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit

class ContentFetcher {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()
    
    suspend fun fetchContent(item: TrackedItem, cookies: String? = null): String? {
        return try {
            val requestBuilder = Request.Builder()
                .url(item.url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
            
            cookies?.let {
                requestBuilder.header("Cookie", it)
            }
            
            val response = client.newCall(requestBuilder.build()).execute()
            if (!response.isSuccessful) return null
            
            val html = response.body?.string() ?: return null
            extractContent(html, item.selector, item.regex)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun extractContent(html: String, selector: String, regex: String?): String {
        val doc: Document = Jsoup.parse(html)
        
        // Support both CSS selectors and simple XPath
        val elements = if (selector.startsWith("//")) {
            // XPath support would require additional library
            // For minimal implementation, convert simple XPath to CSS
            val cssSelector = convertSimpleXPathToCSS(selector)
            doc.select(cssSelector)
        } else {
            doc.select(selector)
        }
        
        // Extract text from all matching elements
        var content = elements.joinToString("\n") { it.text() }
        
        // Apply regex filter if provided
        regex?.let {
            val pattern = Regex(it)
            content = pattern.findAll(content)
                .map { match -> match.value }
                .joinToString("\n")
        }
        
        return content.trim()
    }
    
    private fun convertSimpleXPathToCSS(xpath: String): String {
        // Simple XPath to CSS conversion for common cases
        return xpath
            .replace("//", "")
            .replace("/", " > ")
            .replace("[@class='", ".")
            .replace("']", "")
            .replace("[@id='", "#")
    }
    
    fun extractPageTitle(html: String): String {
        return try {
            val doc = Jsoup.parse(html)
            doc.title().ifEmpty { "Untitled" }
        } catch (e: Exception) {
            "Untitled"
        }
    }
}
