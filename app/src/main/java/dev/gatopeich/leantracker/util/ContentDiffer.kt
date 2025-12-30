package dev.gatopeich.leantracker.util

import android.text.Html
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

object ContentDiffer {
    fun createDiff(oldContent: String?, newContent: String): AnnotatedString {
        if (oldContent == null) {
            return buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFF00AA00))) {
                    append(newContent)
                }
            }
        }
        
        val oldLines = oldContent.lines()
        val newLines = newContent.lines()
        
        return buildAnnotatedString {
            var oldIndex = 0
            var newIndex = 0
            
            while (oldIndex < oldLines.size || newIndex < newLines.size) {
                when {
                    oldIndex >= oldLines.size -> {
                        // Added lines
                        withStyle(SpanStyle(color = Color(0xFF00AA00))) {
                            append("+ ${newLines[newIndex]}\n")
                        }
                        newIndex++
                    }
                    newIndex >= newLines.size -> {
                        // Removed lines
                        withStyle(SpanStyle(color = Color(0xFFAA0000))) {
                            append("- ${oldLines[oldIndex]}\n")
                        }
                        oldIndex++
                    }
                    oldLines[oldIndex] == newLines[newIndex] -> {
                        // Unchanged lines
                        append("  ${oldLines[oldIndex]}\n")
                        oldIndex++
                        newIndex++
                    }
                    else -> {
                        // Changed lines
                        withStyle(SpanStyle(color = Color(0xFFAA0000))) {
                            append("- ${oldLines[oldIndex]}\n")
                        }
                        withStyle(SpanStyle(color = Color(0xFF00AA00))) {
                            append("+ ${newLines[newIndex]}\n")
                        }
                        oldIndex++
                        newIndex++
                    }
                }
            }
        }
    }
    
    fun hasSignificantChange(oldContent: String?, newContent: String): Boolean {
        if (oldContent == null) return true
        
        // Normalize whitespace for comparison
        val oldNormalized = oldContent.trim().replace("\\s+".toRegex(), " ")
        val newNormalized = newContent.trim().replace("\\s+".toRegex(), " ")
        
        return oldNormalized != newNormalized
    }
}
