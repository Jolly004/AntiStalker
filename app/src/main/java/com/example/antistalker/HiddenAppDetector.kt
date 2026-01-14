package com.example.antistalker

import android.content.Context
import android.content.pm.PackageManager

class HiddenAppDetector(private val context: Context) {

    fun isHidden(packageName: String): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            launchIntent == null
        } catch (e: Exception) {
            false
        }
    }
    
    // Known stalkerware package names (example list - would be larger in production)
    private val knownStalkerwarePackages = listOf(
        "com.monitor.app",
        "com.spy.tracker",
        "com.cerberus.android",
        "com.flexispy.android"
    )

    fun isKnownStalkerware(packageName: String): Boolean {
        return knownStalkerwarePackages.contains(packageName)
    }
}
