package dev.gatopeich.leantracker

import android.app.Application
import androidx.work.WorkManager
import dev.gatopeich.leantracker.worker.ContentCheckWorker

class LeanTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Schedule periodic content checks
        ContentCheckWorker.scheduleNextCheck(this)
    }
}
