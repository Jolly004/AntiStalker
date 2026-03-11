package com.example.antistalker

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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

    private fun requestUninstall(packageName: String) {
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

            if (isAdmin) {
                pendingUninstallPackage = packageName
                // Guide user to remove admin rights first
                android.app.AlertDialog.Builder(this)
                    .setTitle("Device Admin Detected")
                    .setMessage("This app is a Device Administrator. You must deactivate its admin rights before uninstalling it.\n\nTap OK to go to Device Admin settings.")
                    .setPositiveButton("OK") { _, _ ->
                        try {
                            // Try manufacturer specific intents first
                            val manufacturer = android.os.Build.MANUFACTURER.lowercase()
                            val intent = when {
                                manufacturer.contains("samsung") -> Intent().setComponent(android.content.ComponentName("com.android.settings", "com.android.settings.Settings\$DeviceAdminSettingsActivity"))
                                manufacturer.contains("xiaomi") -> Intent().setComponent(android.content.ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings"))
                                else -> Intent(Settings.ACTION_SECURITY_SETTINGS)
                            }
                            // If specific component fails, it will throw, catch block handles fallback
                            startActivityForResult(intent, REQUEST_CODE_DEVICE_ADMIN)
                        } catch (e: Exception) {
                            // Fallback to general security settings
                            try {
                                startActivityForResult(Intent(Settings.ACTION_SECURITY_SETTINGS), REQUEST_CODE_DEVICE_ADMIN)
                            } catch (e2: Exception) {
                                startActivityForResult(Intent(Settings.ACTION_SETTINGS), REQUEST_CODE_DEVICE_ADMIN)
                            }
                        }
                    }
                    .setNegativeButton("Cancel") { _, _ -> pendingUninstallPackage = null }
                    .show()
            } else {
                // Standard Uninstall
                val intent = Intent(Intent.ACTION_DELETE)
                intent.data = Uri.parse("package:$packageName")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                    val intent = Intent(Intent.ACTION_DELETE)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
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
                
                val tvLastScan: android.widget.TextView = findViewById(R.id.tvLastScan)
                val tvStatusValue: android.widget.TextView = findViewById(R.id.tvStatusValue)
                updateLastScanText(tvLastScan)
                
                if (scanResults.isEmpty()) {
                    tvNoThreats.visibility = View.VISIBLE
                    rvResults.visibility = View.GONE
                    tvStatusValue.text = "Protected"
                    tvStatusValue.setTextColor(resources.getColor(R.color.safe_green, theme))
                } else {
                    tvNoThreats.visibility = View.GONE
                    rvResults.visibility = View.VISIBLE
                    tvStatusValue.text = "At Risk"
                    tvStatusValue.setTextColor(resources.getColor(R.color.white, theme)) // Or red if defined
                    // Better to use Color.RED or a specific color resource
                    tvStatusValue.setTextColor(android.graphics.Color.parseColor("#FF5252"))
                }
            }
        }
    }

    private fun mapToAppInfo(packageInfo: PackageInfo): AppInfo {
        val pm = packageManager
        val appName = packageInfo.applicationInfo.loadLabel(pm).toString()
        val icon = packageInfo.applicationInfo.loadIcon(pm)
        val isSystemApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        
        // Get Installer Source
        val installerSource = try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                pm.getInstallSourceInfo(packageInfo.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                pm.getInstallerPackageName(packageInfo.packageName)
            }
        } catch (e: Exception) {
            null
        }
        
        val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
        
        return AppInfo(
            packageName = packageInfo.packageName,
            appName = appName,
            icon = icon,
            isSystemApp = isSystemApp,
            installerSource = installerSource,
            permissions = permissions,
            installTime = packageInfo.firstInstallTime,
            lastUpdateTime = packageInfo.lastUpdateTime
        )
    }
}
