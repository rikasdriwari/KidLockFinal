package com.kasbro.kidlock

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.Timer
import kotlin.concurrent.timerTask

class AppMonitorService : Service() {
    private lateinit var prefManager: PrefManager
    private val timer = Timer()

    override fun onCreate() {
        super.onCreate()
        prefManager = PrefManager(this)
        startForeground(101, createNotification())

        timer.schedule(timerTask { checkTime() }, 0, 1000)
    }

    private fun checkTime() {
        // CEK PENANDA DI SINI: Jika terkunci TAPI layar gembok belum tampil
        if (prefManager.isLocked() && !LockActivity.isShowing) {
            val lockIntent = Intent(this, LockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(lockIntent)
        }
    }

    private fun createNotification(): Notification {
        val channelId = "kidlock_service"
        val channel = NotificationChannel(channelId, "KidLock Aktif", NotificationManager.IMPORTANCE_LOW)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("KidLock Aktif")
            .setContentText("Timer sedang berjalan...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}