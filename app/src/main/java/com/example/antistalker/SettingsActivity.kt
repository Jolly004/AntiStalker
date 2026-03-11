package com.example.antistalker

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var switchBackgroundScan: SwitchMaterial
    private lateinit var rgSensitivity: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnBack = findViewById(R.id.btnBack)
        switchDarkMode = findViewById(R.id.switchDarkMode)
        switchBackgroundScan = findViewById(R.id.switchBackgroundScan)
        rgSensitivity = findViewById(R.id.rgSensitivity)

        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)

        // Setup Dark Mode Toggle
        val isDarkMode = prefs.getBoolean("dark_mode", true)
        switchDarkMode.isChecked = isDarkMode
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Setup Background Scan Toggle
        // Default to true if not set
        val isBackgroundScanEnabled = prefs.getBoolean("background_scan", true)
        switchBackgroundScan.isChecked = isBackgroundScanEnabled
        
        // Ensure the worker is scheduled if it's supposed to be enabled (e.g. first run)
        if (isBackgroundScanEnabled) {
            scheduleBackgroundScan()
        }

        switchBackgroundScan.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("background_scan", isChecked).apply()
            if (isChecked) {
                scheduleBackgroundScan()
            } else {
                cancelBackgroundScan()
            }
        }

        // Setup Sensitivity
        val currentSensitivity = prefs.getInt("scan_sensitivity", 5) // Default Medium (5)
        
        when (currentSensitivity) {
            3 -> rgSensitivity.check(R.id.rbHighSensitivity)
            5 -> rgSensitivity.check(R.id.rbMediumSensitivity)
            10 -> rgSensitivity.check(R.id.rbLowSensitivity)
            else -> rgSensitivity.check(R.id.rbMediumSensitivity)
        }

        rgSensitivity.setOnCheckedChangeListener { _, checkedId ->
            val sensitivity = when (checkedId) {
                R.id.rbHighSensitivity -> 3
                R.id.rbMediumSensitivity -> 5
                R.id.rbLowSensitivity -> 10
                else -> 5
            }
            prefs.edit().putInt("scan_sensitivity", sensitivity).apply()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun scheduleBackgroundScan() {
        val scanWorkRequest = PeriodicWorkRequestBuilder<ScanWorker>(24, TimeUnit.HOURS)
            .addTag("daily_scan")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_scan_work",
            androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
            scanWorkRequest
        )
    }

    private fun cancelBackgroundScan() {
        WorkManager.getInstance(this).cancelUniqueWork("daily_scan_work")
    }
}
