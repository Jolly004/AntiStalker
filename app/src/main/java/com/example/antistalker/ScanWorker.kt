package com.example.antistalker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScanWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val heuristicEngine = HeuristicEngine(applicationContext)
                val packageManager = applicationContext.packageManager
                val installedApps = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                val scanResults = mutableListOf<ScanResult>()

                for (packageInfo in installedApps) {
                    if (packageInfo.packageName == applicationContext.packageName) continue

                    val appInfo = mapToAppInfo(packageManager, packageInfo)
                    val result = heuristicEngine.analyzeApp(appInfo)

                    if (result.riskLevel != RiskLevel.SAFE) {
                        scanResults.add(result)
                    }
                }

                // Save results to history
                val historyManager = ScanHistoryManager(applicationContext)
                historyManager.saveScanResult(scanResults)

                // Notify if threats found
                if (scanResults.isNotEmpty()) {
                    val criticalCount = scanResults.count { it.riskLevel == RiskLevel.CRITICAL }
                    val highCount = scanResults.count { it.riskLevel == RiskLevel.HIGH }
                    
                    if (criticalCount > 0 || highCount > 0) {
                        sendNotification(scanResults.size, criticalCount)
                    }
                }

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }

    private fun sendNotification(totalRisks: Int, criticalRisks: Int) {
        val channelId = "antistalker_scan_channel"
        val notificationId = 1001

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Auto-Scan Results"
            val descriptionText = "Notifications for background scans"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = if (criticalRisks > 0) {
            "CRITICAL THREATS FOUND: $criticalRisks. Total risks: $totalRisks. Tap to review."
        } else {
            "Background scan complete. Found $totalRisks potential risks."
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Ensure this resource exists
            .setContentTitle("AntiStalker Security Alert")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
             NotificationManagerCompat.from(applicationContext).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission might not be granted on Android 13+
        }
    }

    private fun mapToAppInfo(pm: PackageManager, packageInfo: PackageInfo): AppInfo {
        val appName = packageInfo.applicationInfo.loadLabel(pm).toString()
        val icon = packageInfo.applicationInfo.loadIcon(pm)
        val isSystemApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        
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
            lastUpdateTime = packageInfo.lastUpdateTime
        )
    }
}
