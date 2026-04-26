package com.example.antistalker

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class RunningAppsActivity : AppCompatActivity() {

    private lateinit var rvRunningApps: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var btnGrantPermission: Button
    private lateinit var btnBack: ImageButton
    private lateinit var adapter: ScanResultAdapter
    private lateinit var heuristicEngine: HeuristicEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_apps)

        rvRunningApps = findViewById(R.id.rvRunningApps)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        btnGrantPermission = findViewById(R.id.btnGrantPermission)
        btnBack = findViewById(R.id.btnBack)
        
        heuristicEngine = HeuristicEngine(this)

        btnBack.setOnClickListener { finish() }

        btnGrantPermission.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        adapter = ScanResultAdapter(
            emptyList(),
            onItemClick = { result ->
                openAppInfo(result.appInfo.packageName)
            },
            onUninstallClick = { result ->
                requestUninstall(result.appInfo.packageName)
            }
        )
        
        rvRunningApps.layoutManager = LinearLayoutManager(this)
        rvRunningApps.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (hasUsageStatsPermission()) {
            btnGrantPermission.visibility = View.GONE
            loadRunningApps()
        } else {
            btnGrantPermission.visibility = View.VISIBLE
            tvEmptyState.text = "Permission required to see running apps."
            tvEmptyState.visibility = View.VISIBLE
            rvRunningApps.visibility = View.GONE
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun loadRunningApps() {
        CoroutineScope(Dispatchers.IO).launch {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1) // Last 24 hours
            val start = calendar.timeInMillis
            val end = System.currentTimeMillis()

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                end
            )

            val runningPackages = stats.map { it.packageName }.toSet()
            val pm = packageManager
            val riskyRunningApps = mutableListOf<ScanResult>()

            for (packageName in runningPackages) {
                if (packageName == this@RunningAppsActivity.packageName) continue
                
                try {
                    val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                    val appInfo = mapToAppInfo(pm, packageInfo)
                    val result = heuristicEngine.analyzeApp(appInfo)

                    // Only show if it has some risk
                    if (result.riskLevel != RiskLevel.SAFE) {
                        riskyRunningApps.add(result)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    // App might be uninstalled
                }
            }
            
            riskyRunningApps.sortBy { it.riskLevel }

            withContext(Dispatchers.Main) {
                if (riskyRunningApps.isEmpty()) {
                    tvEmptyState.text = "No suspicious running apps found."
                    tvEmptyState.visibility = View.VISIBLE
                    rvRunningApps.visibility = View.GONE
                } else {
                    tvEmptyState.visibility = View.GONE
                    rvRunningApps.visibility = View.VISIBLE
                    adapter.updateData(riskyRunningApps)
                }
            }
        }
    }

    private fun openAppInfo(packageName: String) {
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

    private fun launchUninstallIntent(packageName: String) {
        // Unified flow: always send the user to the App Info page where they can
        // manually tap Uninstall (or Disable for system apps). Android pops back
        // to AntiStalker automatically once the target app is gone.
        Toast.makeText(
            this,
            "Tap Uninstall (or Disable) on the next screen to remove this app.",
            Toast.LENGTH_LONG
        ).show()
        openAppInfo(packageName)
    }

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
                launchUninstallIntent(packageName)
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
                if (!isAdmin) pendingUninstallPackage = null
            }
        }
    }

    private fun mapToAppInfo(pm: PackageManager, packageInfo: android.content.pm.PackageInfo): AppInfo {
        val appName = packageInfo.applicationInfo.loadLabel(pm).toString()
        val icon = packageInfo.applicationInfo.loadIcon(pm)
        // FLAG_SYSTEM OR FLAG_UPDATED_SYSTEM_APP — see MainActivity.mapToAppInfo for the full reason.
        val isSystemApp = (packageInfo.applicationInfo.flags and
            (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0
        
        val installerSource = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
            lastU