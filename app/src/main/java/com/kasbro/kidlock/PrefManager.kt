package com.kasbro.kidlock

import android.content.Context
import androidx.core.content.edit

class PrefManager(context: Context) {
    private val prefs = context.getSharedPreferences("KidLockPrefs", Context.MODE_PRIVATE)

    fun setTimer(minutes: Int) {
        val endTime = System.currentTimeMillis() + (minutes * 60 * 1000)
        prefs.edit { putLong("LOCK_TIME", endTime) }
    }

    fun isLocked(): Boolean {
        return System.currentTimeMillis() > prefs.getLong("LOCK_TIME", Long.MAX_VALUE)
    }

    fun clearTimer() {
        prefs.edit { putLong("LOCK_TIME", Long.MAX_VALUE) }
    }
}