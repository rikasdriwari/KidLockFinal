package com.kasbro.kidlock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class LockActivity : AppCompatActivity() {
    private lateinit var prefManager: PrefManager
    private lateinit var pinManager: PinManager

    // PENANDA STATUS LAYAR
    companion object {
        var isShowing = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        prefManager = PrefManager(this)
        pinManager = PinManager(this)

        if (!pinManager.isPinSet()) {
            pinManager.savePin("1234")
        }

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnUnlock = findViewById<Button>(R.id.btnUnlock)

        btnUnlock.setOnClickListener {
            val inputPin = etPin.text.toString()

            if (pinManager.checkPin(inputPin)) {
                prefManager.clearTimer()
                Toast.makeText(this, "Kunci Terbuka!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "PIN Salah!", Toast.LENGTH_SHORT).show()
                etPin.text.clear()
            }
        }
    }

    // Mengaktifkan penanda saat gembok tampil di layar
    override fun onResume() {
        super.onResume()
        isShowing = true
    }

    // Mematikan penanda saat anak mencoba menekan tombol Home/Recent Apps
    override fun onPause() {
        super.onPause()
        isShowing = false
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (prefManager.isLocked()) {
            val intent = Intent(this, LockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
        }
    }
}