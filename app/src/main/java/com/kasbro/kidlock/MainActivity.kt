package com.kasbro.kidlock

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    private lateinit var prefManager: PrefManager
    private lateinit var pinManager: PinManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefManager = PrefManager(this)
        pinManager = PinManager(this)

        if (!pinManager.isPinSet()) {
            pinManager.savePin("1234")
        }

        checkPermissions()

        val btn15m = findViewById<Button>(R.id.btn15m)
        val btn30m = findViewById<Button>(R.id.btn30m)
        val etCustomTime = findViewById<EditText>(R.id.etCustomTime)
        val btnCustom = findViewById<Button>(R.id.btnCustom)
        val btnChangePin = findViewById<Button>(R.id.btnChangePin)

        btn15m.setOnClickListener { startTimer(15) }
        btn30m.setOnClickListener { startTimer(30) }

        btnCustom.setOnClickListener {
            val customMinutes = etCustomTime.text.toString()
            if (customMinutes.isNotEmpty()) {
                startTimer(customMinutes.toInt())
            } else {
                Toast.makeText(this, "Masukkan angka menit dulu!", Toast.LENGTH_SHORT).show()
            }
        }

        btnChangePin.setOnClickListener {
            showChangePinDialog()
        }
    }

    private fun showChangePinDialog() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        input.hint = "Masukkan angka PIN baru"
        input.setPadding(40, 40, 40, 40)

        AlertDialog.Builder(this)
            .setTitle("Ubah PIN")
            .setMessage("Silakan masukkan PIN baru untuk pengaman gembok:")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val newPin = input.text.toString()
                if (newPin.isNotEmpty()) {
                    pinManager.savePin(newPin)
                    Toast.makeText(this, "PIN berhasil diubah!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "PIN tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun checkPermissions() {
        // Hanya menyisakan izin Tampil di Atas Aplikasi Lain (Wajib untuk Gembok)
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Izinkan tampil di atas aplikasi lain!", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()))
        }
    }

    private fun startTimer(minutes: Int) {
        prefManager.setTimer(minutes)
        val serviceIntent = Intent(this, AppMonitorService::class.java)
        startForegroundService(serviceIntent)
        Toast.makeText(this, "Aplikasi terkunci dalam $minutes menit", Toast.LENGTH_LONG).show()
        finish()
    }
}