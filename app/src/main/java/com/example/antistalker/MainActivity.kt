package com.example.antistalker

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var btnScan: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var rvResults: RecyclerView
    private lateinit var tvNoThreats: android.widget.TextView
    private lateinit var adapter: ScanResultAdapter
    private lateinit var heuristicEngine: HeuristicEngine
    private lateinit var historyManager: ScanHistoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnMenu = findViewById(R.id.btnMenu)
        btnScan = findViewById(R.id.btnScan)
        progressBar = findViewById(R.id.progressBar)
        rvResults = findViewById(R.id.rvResults)
        tvNoThreats = findViewById(R.id.tvNoThreats)
        val tvLastScan: android.widget.TextView = findViewById(R.id.tvLastScan)

        heuristicEngine = HeuristicEngine(this)
        historyManager = ScanHistoryManager(this)

        updateLastScanText(tvLastScan)

        // Setup Drawer Menu
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Already on Home
                }
                R.id.nav_running_apps -> {
                    startActivity(Intent(this, RunningAppsActivity::class.java))
                }
                R.id.nav_previous_scans -> {
                    startActivity(Intent(this, PreviousScansActivity::class.java))
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        
        adapter = ScanResultAdapter(
            emptyList(),
            onItemClick = { result ->
                openAppSettings(result.appInfo.packageName)
            },
            onUninstallClick = { result ->
                requestUninstall(result.appInfo.packageName)
            }
        )
        
        rvResults.layoutManager = LinearLayoutManager(this)
        rvResults.adapter = adapter

        btnScan.setOnClickListener {
            performScan()
        }
    }

    override fun onResume() {
        super.onResume()
        val tvLastScan: android.widget.TextView = findViewById(R.id.tvLastScan)
        updateLastScanText(tvLastScan)
        
        // Reset status to "Scan Needed" if we don't have active results in memory
        // Ideally we would persist the last scan result state, but for now this avoids the "Protected" lie.
        val tvStatusValue: android.widget.TextView = findViewById(R.id.tvStatusValue)
        if (tvStatusValue.text == "Protected" && adapter.itemCount == 0) {
             // If it says Protected but list is empty (and we haven't just scanned), it might be stale or default.
             // Actually, if list is empty, Protected is correct IF we just scanned.
             // But on cold start, list is empty and we haven't scanned.
             // We can check if lastScanTime is recent?
             // For now, let's just leave it as "Scan Needed" from XML default unless performScan updates it.
             // But wait, onResume is called after performScan too? No, performScan is async.
             // If we rotate, activity recreates.
             // We need to restore state.
             // For this iteration, I'll just ensure the XML default is "Scan Needed" (done)
             // and here we only update Last Scan time.
        }
    }

    private fun updateLastScanText(tvLastScan: android.widget.TextView) {
        val lastScanTime = historyManager.getLastScanTimestamp()
        if (lastScanTime > 0) {
            val sdf = java.text.SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale.getDefault())
            tvLastScan.text = "Last scan: ${sdf.format(java.util.Date(lastScanTime))}"
        } else {
            tvLastScan.text = "Last scan: Never"
        }
    }

    private fun openAppSettings(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Request code for Device Admin settings
    private val REQUEST_CODE_DEVICE_ADMIN = 1001
    private var pendingUninstallPackage: String? = null

    /**
     * Tries to open the system "Device admin apps" page directly, where the user
     * can disable admin rights for a specific app. Different OEMs and Android
     * versions ship different Settings activities, so we probe a list of known
     * ComponentNames in order and launch the first one that resolves on this device.
     *
     * @return true if we landed on a specific Device-admin page, false if we had
     *         to fall back to a generic Security Settings page (so the caller
     *         can show extra guidance to the user).
     */
    private fun openDeviceAdminSettings(): Boolean {
        Log.d("AntiStalker", "[Main] openDeviceAdminSettings()")
        // Ordered most-specific to least-specific. Most modern devices (Pixel,
        // Samsung One UI, current Xiaomi/Oppo/OnePlus) all fork AOSP Settings,
        // so the first or second entry usually wins.
        val candidates = listOf(
            // Canonical AOSP target (Pixel, modern Samsung One UI, etc.)
            android.content.ComponentName(
                "com.android.settings",
                "com.android.settings.Settings\$DeviceAdminSettingsActivity"
            ),
            // Legacy alias kept by AOSP for backward-compatible shortcuts
            android.content.ComponentName(
                "com.android.settings",
                "com.android.settings.DeviceAdminSettings"
            ),
            // Path used by some forks where the activity sits under specialaccess
            android.content.ComponentName(
                "com.android.settings",
                "com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminListActivity"
            ),
            // Older MIUI and a few other custom skins
            android.content.ComponentName(
                "com.android.settings",
                "com.android.settings.password.DeviceAdminSettings"
            ),
            // Samsung-namespaced Settings (rare — only some older One UI builds)
            android.content.ComponentName(
                "com.samsung.android.settings",
                "com.samsung.android.settings.Settings\$DeviceAdminSettingsActivity"
            )
        )

        for (component in candidates) {
            val intent = Intent().setComponent(component)
            val resolved = intent.resolveActivity(packageManager)
            Log.d("AntiStalker", "[Main]   probe ${component.flattenToShortString()} → $resolved")
            if (resolved != null) {
                try {
                    startActivityForResult(intent, REQUEST_CODE_DEVICE_ADMIN)
                    Log.d("AntiStalker", "[Main]   launched ${component.flattenToShortString()}")
                    return true
                } catch (e: Exception) {
                    Log.w("AntiStalker", "[Main]   launch failed for ${component.flattenToShortString()}", e)
                    // Permission or other launch failure — try the next candidate.
                }
            }
        }

        // No specific Device-admin page resolved. Fall back to Security Settings.
        Log.w("AntiStalker", "[Main]   no specific admin page resolved, falling back to Security Settings")
        return try {
            startActivityForResult(Intent(Settings.ACTION_SECURITY_SETTINGS), REQUEST_CODE_DEVICE_ADMIN)
            false
        } catch (e: Exception) {
            Log.e("AntiStalker", "[Main]   Security Settings failed too, falling back to top-level Settings", e)
            startActivityForResult(Intent(Settings.ACTION_SETTINGS), REQUEST_CODE_DEVICE_ADMIN)
            false
        }
    }

    private fun launchUninstallIntent(packageName: String) {
        Log.d("AntiStalker", "[Main] launchUninstallIntent($packageName) → App Info")
        // Unified flow: always send the user to the App Info page, regardless of
        // whether the target is a user app or a system app. From there they can
        // manually tap Uninstall (user apps) or Disable / Uninstall updates
        // (system apps) — Android handles the result correctly in both cases.
        // When the target app disappears, Android automatically pops App Info
        // and returns the user to AntiStalker.
        Toast.makeText(
            this,
            "Tap Uninstall (or Disable) on the next screen to remove this app.",
            Toast.LENGTH_LONG
        ).show()
        openAppSettings(packageName)
    }

    private fun requestUninstall(packageName: String) {
        Log.d("AntiStalker", "[Main] requestUninstall($packageName) called")
        try {
            // Check if it's a Device Admin
            val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
            val admins = dpm.activeAdmins
            var isAdmin = false

            if (admins != null) {
                for (admin in admins) {
                    if (admin.packageName == packageName) {
                        isAdmin = true
                        break
                    }
                }
            }
            Log.d("AntiStalker", "[Main]   isAdmin=$isAdmin")

            if (isAdmin) {
                pendingUninstallPackage = packageName
                // Guide user to remove admin rights first
                android.app.AlertDialog.Builder(this)
                    .setTitle("Device Admin Detected")
                    .setMessage("This app is a Device Administrator. You must deactivate its admin rights before uninstalling it.\n\nTap OK to go to Device Admin settings.")
                    .setPositiveButton("OK") { _, _ ->
                        val landedOnAdminPage = openDeviceAdminSettings()
                        if (!landedOnAdminPage) {
                            // We had to fall back to a generic Settings screen — give the
                            // user a hint about what to look for so they don't get stuck.
                            Toast.makeText(
                                this,
                                "Could not open Device admin apps directly. Look for \"Device admin apps\" or \"Security\" in Settings.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .setNegativeButton("Cancel") { _, _ -> pendingUninstallPackage = null }
                    .show()
            } else {
                launchUninstallIntent(packageName)
            }
        } catch (e: Exception) {
            Log.e("AntiStalker", "[Main] requestUninstall threw", e)
            Toast.makeText(this, "Error requesting uninstall: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DEVICE_ADMIN) {
            // User returned from Device Admin settings
            pendingUninstallPackage?.let { packageName ->
                // Check if admin rights were actually revoked
                val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
                val admins = dpm.activeAdmins
                var isAdmin = false
                if (admins != null) {
                    for (admin in admins) {
                        if (admin.packageName == packageName) {
                            isAdmin = true
                            break
                        }
                    }
                }

                if (!isAdmin) {
                    // Admin rights revoked! Proceed to uninstall.
                    launchUninstallIntent(packageName)
                } else {
                    // Still admin
                    // Prompt user again if they failed to disable it
                    android.app.AlertDialog.Builder(this)
                        .setTitle("Admin Rights Still Active")
                        .setMessage("It looks like the app is still a Device Administrator. You cannot uninstall it until this is disabled.\n\nWould you like to try again?")
                        .setPositiveButton("Try Again") { _, _ ->
                            requestUninstall(packageName) // Retry the flow
                        }
                        .setNegativeButton("Cancel") { _, _ -> pendingUninstallPackage = null }
                        .show()
                }
                // Don't null pendingUninstallPackage immediately if we might retry, 
                // but here we are restarting the flow so it's safer to clear it or let the new flow set it.
                // Actually, if we retry, requestUninstall sets it again.
                if (!isAdmin) pendingUninstallPackage = null
            }
        }
    }

    private fun performScan() {
        btnScan.isEnabled = false
        progressBar.visibility = View.VISIBLE
        tvNoThreats.visibility = View.GONE
        rvResults.visibility = View.GONE // Hide list while scanning
        adapter.updateData(emptyList())

        lifecycleScope.launch(Dispatchers.IO) {
            val installedApps = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            val scanResults = mutableListOf<ScanResult>()

            for (packageInfo in installedApps) {
                // Skip our own app
                if (packageInfo.packageName == packageName) continue

                val appInfo = mapToAppInfo(packageInfo)
                val result = heuristicEngine.analyzeApp(appInfo)

                // Filter to show only risks for now, or all if needed. 
                // Showing SAFE apps might clutter the view, but let's show everything sorted by risk.
                if (result.riskLevel != RiskLevel.SAFE) {
                    scanResults.add(result)
                }
            }

            // Sort by risk: HIGH -> MEDIUM -> LOW
            scanResults.sortBy { it.riskLevel } // enum ordinal: HIGH=0, MEDIUM=1...

            // Save History
            historyManager.saveScanResult(scanResults)

            withContext(Dispatchers.Main) {
                adapter.updateData(scanResults)
                progressBar.visibility = View.GONE
                btnScan.isEnabled = true
                
                val tvLastScan: android.widget.TextView = findVi