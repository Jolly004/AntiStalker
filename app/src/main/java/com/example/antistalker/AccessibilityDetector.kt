package com.example.antistalker

import android.accessibilityservice.AccessibilityService
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo

class AccessibilityDetector {

    fun requestsAccessibilityService(packageManager: PackageManager, packageName: String): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SERVICES)
            val services = packageInfo.services ?: return false

            for (service in services) {
                if (service.permission == android.Manifest.permission.BIND_ACCESSIBILITY_SERVICE) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }
}
