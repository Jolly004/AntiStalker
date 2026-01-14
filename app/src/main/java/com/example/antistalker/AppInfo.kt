package com.example.antistalker

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isSystemApp: Boolean,
    val installerSource: String?,
    val permissions: List<String>,
    val installTime: Long,
    val lastUpdateTime: Long
)
