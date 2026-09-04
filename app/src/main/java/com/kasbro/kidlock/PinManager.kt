package com.kasbro.kidlock

import android.content.Context
import androidx.core.content.edit

class PinManager(context: Context) {
    private val prefs = context.getSharedPreferences("KidLockPrefs", Context.MODE_PRIVATE)

    fun savePin(pin: String) {
        prefs.edit { putString("PARENT_PIN", pin) }
    }

    fun checkPin(pin: String): Boolean {
        return prefs.getString("PARENT_PIN", "") == pin
    }

    fun isPinSet(): Boolean {
        return prefs.getString("PARENT_PIN", "")?.isNotEmpty() == true
    }
}