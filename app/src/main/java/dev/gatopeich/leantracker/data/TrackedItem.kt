package dev.gatopeich.leantracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_items")
data class TrackedItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val name: String,
    val selector: String, // CSS selector or XPath
    val regex: String? = null,
    val intervalHours: Int = 24,
    val lastContent: String? = null,
    val lastCheckedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val hasChange: Boolean = false
)
