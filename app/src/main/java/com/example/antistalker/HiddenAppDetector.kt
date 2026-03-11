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
    
    fun isKnownStalkerware(packageName: String): Boolean {
        return StalkerwareDefinitions.KNOWN_PACKAGES.contains(packageName)
    }
}
