package dev.gatopeich.leantracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import dev.gatopeich.leantracker.MainActivity
import dev.gatopeich.leantracker.R
import dev.gatopeich.leantracker.data.AppDatabase
import dev.gatopeich.leantracker.domain.ContentFetcher
import dev.gatopeich.leantracker.domain.TrackedItemRepository
import dev.gatopeich.leantracker.util.ContentDiffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class ContentCheckWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val database = AppDatabase.getDatabase(context)
    private val repository = TrackedItemRepository(database)
    private val fetcher = ContentFetcher()
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val items = repository.getAllItemsList()
            
            items.forEach { item ->
                // Check if it's time to poll this item based on its interval
                val now = System.currentTimeMillis()
                val intervalMs = item.intervalHours * 60 * 60 * 1000L
                val lastChecked = item.lastCheckedAt ?: 0L
                
                if (now - lastChecked >= intervalMs) {
                    checkItem(item)
                }
            }
            
            // Schedule next check
            scheduleNextCheck(context)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private suspend fun checkItem(item: dev.gatopeich.leantracker.data.TrackedItem) {
        // Load auth cookies if available for this domain
        val authCookie = database.authCookieDao().getByDomain(getDomain(item.url))
        val cookies = authCookie?.cookies
        
        val newContent = fetcher.fetchContent(item, cookies) ?: return
        
        // Check if content has changed significantly
        val hasChange = ContentDiffer.hasSignificantChange(item.lastContent, newContent)
        
        if (hasChange && item.lastContent != null) {
            // Notify user of the change
            sendNotification(item.name, item.id)
        }
        
        // Update item with new content and timestamp
        repository.updateItem(
            item.copy(
                lastContent = newContent,
                lastCheckedAt = System.currentTimeMillis(),
                hasChange = hasChange && item.lastContent != null
            )
        )
    }
    
    private fun getDomain(url: String): String {
        return try {
            val uri = java.net.URI(url)
            uri.host
        } catch (e: Exception) {
            url
        }
    }
    
    private fun sendNotification(itemName: String, itemId: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("item_id", itemId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, itemId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.content_changed))
            .setContentText(context.getString(R.string.change_detected, itemName))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(itemId.toInt(), notification)
    }
    
    companion object {
        private const val CHANNEL_ID = "content_changes"
        private const val WORK_NAME = "content_check_work"
        
        fun scheduleNextCheck(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<ContentCheckWorker>(
                1, TimeUnit.HOURS // Check every hour, will skip items not due
            )
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
        }
    }
}
