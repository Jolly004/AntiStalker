package com.example.antistalker

import android.content.Context

class HeuristicEngine(private val context: Context) {
    
    private val permissionAnalyzer = PermissionAnalyzer()
    private val hiddenAppDetector = HiddenAppDetector(context)
    private val adminAccessDetector = AdminAccessDetector(context)
    private val accessibilityDetector = AccessibilityDetector()

    fun analyzeApp(appInfo: AppInfo): ScanResult {
        // --- SIDELOAD CHECK ---
        // Trusted Installers: Google Play Store
        val isTrustedInstaller = appInfo.installerSource == "com.android.vending" ||
                                 appInfo.installerSource == "com.google.android.feedback" // Sometimes used by older play services

        val isSideloaded = !appInfo.isSystemApp && !isTrustedInstaller

        // 0. Whitelist Check
        // Only whitelist if it's a System App OR (It's a User App AND Installed from Play Store)
        // If it is sideloaded, we DO NOT whitelist it based on name alone.
        if (Whitelist.isWhitelisted(appInfo.packageName, appInfo.isSystemApp) && !isSideloaded) {
            return ScanResult(appInfo, RiskLevel.SAFE, emptyList())
        }

        val riskFactors = mutableListOf<String>()
        var riskScore = 0
        
        if (isSideloaded) {
            riskFactors.add("Sideloaded Application (Untrusted Source)")
            riskScore += 3
        }

        // 1. Permission Analysis
        val permissionRisks = permissionAnalyzer.analyzePermissions(appInfo.permissions)
        if (permissionRisks.isNotEmpty()) {
            riskFactors.addAll(permissionRisks)
            riskScore += permissionRisks.size * 2
        }
        
        val dangerousCount = permissionAnalyzer.getDangerousPermissionsCount(appInfo.permissions)
        // Lower threshold for Sideloaded apps
        val threshold = if (isSideloaded) 1 else 3 
        
        if (dangerousCount > threshold) {
             riskFactors.add("Excessive Dangerous Permissions ($dangerousCount)")
             riskScore += 2
        }

        // 2. Hidden App Check
        if (hiddenAppDetector.isHidden(appInfo.packageName) && !appInfo.isSystemApp) {
            // Only flag if it's NOT a system app. System apps often don't have launcher icons.
            // Even then, many valid background apps don't have icons.
            // We combine this with permission checks.
            if (dangerousCount > 0) {
                 riskFactors.add("Hidden App with Dangerous Permissions")
                 riskScore += 5
            }
            // Sideloaded + Hidden = Huge Red Flag
            if (isSideloaded) {
                riskFactors.add("Sideloaded & Hidden (High Probability of Malware)")
                riskScore += 10
            }
        }
        
        if (hiddenAppDetector.isKnownStalkerware(appInfo.packageName)) {
            riskFactors.add("KNOWN STALKERWARE SIGNATURE")
            riskScore += 20
        }

        // 3. Admin Access
        if (adminAccessDetector.isDeviceAdmin(appInfo.packageName)) {
            riskFactors.add("Has Device Admin Privileges")
            riskScore += 3
            
            // If it's a hidden user app with Admin privs, it's very dangerous
            if (hiddenAppDetector.isHidden(appInfo.packageName) && !appInfo.isSystemApp) {
                riskFactors.add("Hidden Device Admin (Malware Indicator)")
                riskScore += 10
            }
        }

        // 4. Accessibility Service
        if (accessibilityDetector.requestsAccessibilityService(context.packageManager, appInfo.packageName)) {
            riskFactors.add("Requests Accessibility Service")
            // Accessibility service is a high-risk feature often used by stalkerware
            riskScore += 5
            
            if (hiddenAppDetector.isHidden(appInfo.packageName) && !appInfo.isSystemApp) {
                riskFactors.add("Hidden App with Accessibility Service (High Risk Indicator)")
                riskScore += 10
            }
        }
        
        // Determine Risk Level
        val riskLevel = when {
            riskScore >= 20 || dangerousCount >= 6 -> RiskLevel.CRITICAL
            riskScore >= 10 -> RiskLevel.HIGH
            riskScore >= 5 -> RiskLevel.MEDIUM
            riskScore >= 1 -> RiskLevel.LOW
            else -> RiskLevel.SAFE
        }

        return ScanResult(appInfo, riskLevel, riskFactors)
    }
}
