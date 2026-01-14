package com.example.antistalker

import android.Manifest

class PermissionAnalyzer {

    private val dangerousPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.SYSTEM_ALERT_WINDOW, // Overlay permission (often used to hide)
        Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, // To run in background forever
        Manifest.permission.FOREGROUND_SERVICE,
        "android.permission.ACCESS_BACKGROUND_LOCATION",
        "android.permission.QUERY_ALL_PACKAGES",
        "android.permission.PACKAGE_USAGE_STATS" // Monitor other apps
    )

    fun analyzePermissions(permissions: List<String>): List<String> {
        val risks = mutableListOf<String>()
        val hasLocation = permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) || permissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION)
        val hasSms = permissions.contains(Manifest.permission.READ_SMS) || permissions.contains(Manifest.permission.RECEIVE_SMS) || permissions.contains(Manifest.permission.SEND_SMS)
        val hasCallLog = permissions.contains(Manifest.permission.READ_CALL_LOG)
        val hasAudio = permissions.contains(Manifest.permission.RECORD_AUDIO)
        val hasCamera = permissions.contains(Manifest.permission.CAMERA)
        val hasOverlay = permissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)
        val hasUsageStats = permissions.contains("android.permission.PACKAGE_USAGE_STATS")
        val ignoresBattery = permissions.contains(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)

        if (hasLocation && hasSms) {
            risks.add("Tracks Location and SMS (High Risk)")
        }
        if (hasLocation && hasAudio) {
            risks.add("Tracks Location and Audio (Spying)")
        }
        if (hasSms && hasCallLog) {
            risks.add("Monitors Communication (SMS & Calls)")
        }
        if (hasOverlay && ignoresBattery) {
            risks.add("Persistent Overlay (Hidden Operation)")
        }
        if (hasUsageStats) {
             risks.add("Monitors Other App Usage")
        }
        
        // Individual checks
        if (permissions.contains("android.permission.ACCESS_BACKGROUND_LOCATION")) {
            risks.add("Always-on Location Tracking")
        }
        
        return risks
    }
    
    fun getDangerousPermissionsCount(permissions: List<String>): Int {
        return permissions.count { dangerousPermissions.contains(it) }
    }
}
