package com.example.antistalker

import android.content.Context

class HeuristicEngine(private val context: Context) {
    
    private val permissionAnalyzer = PermissionAnalyzer()
    private val hiddenAppDetector = HiddenAppDetector(context)
    private val adminAccessDetector = AdminAccessDetector(context)
    private val accessibilityDetector = AccessibilityDetector()

    fun analyzeApp(appInfo: AppInfo): ScanResult {
        // Get Sensitivity Settings
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val sensitivity = prefs.getInt("scan_sensitivity", 10) // Default 10 (Low / Lenient)

        // Define Thresholds based on Sensitivity
        // Sensitivity 3 (High/Aggressive) -> Lower thresholds
        // Sensitivity 5 (Medium/Default) -> Standard thresholds
        // Sensitivity 10 (Low/Lenient) -> Higher thresholds
        
        val criticalThreshold = if (sensitivity == 3) 15 else if (sensitivity == 10) 30 else 20
        val highThreshold = if (sensitivity == 3) 7 else if (sensitivity == 10) 20 else 10
        val mediumThreshold = if (sensitivity == 3) 3 else if (sensitivity == 10) 10 else 5
        
        // --- SIDELOAD CHECK ---
        // Trusted Installers: Google Play Store + major OEM / alternative app stores.
        // Stalkerware almost never distributes through these because they all have a
        // review process; treating them as trusted dramatically cuts false positives
        // on Samsung, Huawei, Xiaomi etc. devices where many legit apps come pre-bundled
        // through the OEM's own store rather than Play.
        val trustedInstallers = setOf(
            "com.android.vending",                  // Google Play Store
            "com.google.android.feedback",          // Older Play Services
            "com.sec.android.app.samsungapps",      // Samsung Galaxy Store
            "com.amazon.venezia",                   // Amazon Appstore
            "com.amazon.mShop.android.shopping",    // Amazon Appstore (newer)
            "com.huawei.appmarket",                 // Huawei AppGallery
            "com.xiaomi.market",                    // Xiaomi GetApps
            "com.heytap.market",                    // Oppo / OnePlus App Market
            "com.oppo.market",                      // Oppo Market (legacy)
            "org.fdroid.fdroid"                     // F-Droid
        )
        val isTrustedInstaller = trustedInstallers.contains(appInfo.installerSource)

        val isSideloaded = !appInfo.isSystemApp && !isTrustedInstaller

        // 0a. Known-stalkerware signature check runs BEFORE any allowlist short-circuit
        //     so a stalkerware app that spoofs a popular package name (e.g. com.whatsapp)
        //     can't escape detection by hiding behind the curated allowlist.
        if (hiddenAppDetector.isKnownStalkerware(appInfo.packageName)) {
            return ScanResult(
                appInfo,
                RiskLevel.CRITICAL,
                listOf("KNOWN STALKERWARE SIGNATURE")
            )
        }

        // 0b. System / namespace whitelist
        // Only whitelist if it's a System App OR (It's a User App AND Installed from Play Store)
        // If it is sideloaded, we DO NOT whitelist it based on name alone.
        if (Whitelist.isWhitelisted(appInfo.packageName, appInfo.isSystemApp) && !isSideloaded) {
            return ScanResult(appInfo, RiskLevel.SAFE, emptyList())
        }

        // 0c. Popular consumer apps (WhatsApp, Skype, Telegram, etc.) legitimately request
        //     heavy permission sets. Short-circuit them to SAFE here, AFTER the signature
        //     check above has run. We still apply this even when the app looks "sideloaded"
        //     because OEM stores routinely supply Play-Store-equivalent builds of these apps.
        if (Whitelist.isPopularApp(appInfo.packageName)) {
            return ScanResult(appInfo, RiskLevel.SAFE, emptyList())
        }

        // 0d. Gated brand-prefix trust. Apps under tightly-scoped first-party
        //     namespaces (com.samsung.android.*, com.google.android.apps.*, etc.)
        //     are trusted IFF they don't exhibit any actual stalkerware-shaped
        //     behaviour. This catches legit user-installable extensions like
        //     Samsung Notes Add-ons while still flagging any hypothetical
        //     stalkerware that tried to disguise itself under such a prefix —
        //     because real stalkerware always needs accessibility / admin / a
        //     wide permission grant to function, and any of those signals causes
        //     us to fall through to the normal scoring path below.
        if (Whitelist.isHighConfidenceBrand(appInfo.packageName)) {
            val isAdmin = adminAccessDetector.isDeviceAdmin(appInfo.packageName)
            val hasAccessibility = accessibilityDetector
                .requestsAccessibilityService(context.packageManager, appInfo.packageName)
            val dangerousCountForBrand = permissionAnalyzer
                .getDangerousPermissionsCount(appInfo.permissions)
            // Threshold raised from 5 → 10. Samsung first-party productivity apps
            // like Calendar, Notes, Email, and Voice Reco