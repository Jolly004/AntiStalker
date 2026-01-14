package com.example.antistalker

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context

class AdminAccessDetector(private val context: Context) {

    fun isDeviceAdmin(packageName: String): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val activeAdmins: List<ComponentName>? = dpm.activeAdmins
        
        if (activeAdmins != null) {
            for (admin in activeAdmins) {
                if (admin.packageName == packageName) {
                    return true
                }
            }
        }
        return false
    }
}
