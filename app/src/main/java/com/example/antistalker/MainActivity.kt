package com.example.antistalker

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import android.content.Intent
import android.net.Uri
import android.provider.Settings

class MainActivity : AppCompatActivity() {

    private lateinit var btnScan: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var rvResults: RecyclerView
    private lateinit var tvNoThreats: android.widget.TextView
    private lateinit var adapter: ScanResultAdapter
    private lateinit var heuristicEngine: HeuristicEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnScan = findViewById(R.id.btnScan)
        progressBar = findViewById(R.id.progressBar)
        rvResults = findViewById(R.id.rvResults)
        tvNoThreats = findViewById(R.id.tvNoThreats)

        heuristicEngine = HeuristicEngine(this)
        
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

    private fun openAppSettings(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestUninstall(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
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

            withContext(Dispatchers.Main) {
                adapter.updateData(scanResults)
                progressBar.visibility = View.GONE
                btnScan.isEnabled = true
                
                if (scanResults.isEmpty()) {
                    tvNoThreats.visibility = View.VISIBLE
                    rvResults.visibility = View.GONE
                } else {
                    tvNoThreats.visibility = View.GONE
                    rvResults.visibility = View.VISIBLE
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
