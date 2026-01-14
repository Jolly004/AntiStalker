package com.example.antistalker

import android.content.pm.PackageManager

class ServiceAnalyzer {

    private val suspiciousServiceNames = listOf(
        "monitor", "tracker", "spy", "logger", "sync", "upload", "location"
    )

    fun analyzeServices(packageManager: PackageManager, packageName: String): List<String> {
        val risks = mutableListOf<String>()
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SERVICES)
            val services = packageInfo.services ?: return emptyList()

            for (service in services) {
                // Check for suspicious names (simplified heuristic)
                val serviceName = service.name.lowercase()
                
                // Many legitimate apps use these terms, so we must be careful. 
                // This is a weak signal on its own, but useful in context.
                // for (suspicious in suspiciousServiceNames) {
                //    if (serviceName.contains(suspicious)) {
                //        risks.add("Suspicious Service Name: ${service.name}")
                //    }
                // }
                
                // Better: Check for known stalkerware service signatures if we had a database.
            }
        } catch (e: Exception) {
            // Package not found or other error
        }
        return risks
    }
}
