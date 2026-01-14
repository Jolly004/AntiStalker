package com.example.antistalker

class Whitelist {
    companion object {
        private val safePackages = setOf(
            // --- Google Standard Apps ---
            "com.google.android.gms",                 // Google Play Services
            "com.android.vending",                    // Google Play Store
            "com.google.android.googlequicksearchbox", // Google App
            "com.google.android.as",                  // Android System Intelligence
            "com.google.android.packageinstaller",    // Package Installer
            "com.google.android.apps.messaging",      // Messages
            "com.google.android.dialer",              // Phone
            "com.google.android.calendar",            // Calendar
            "com.google.android.contacts",            // Contacts
            "com.google.android.deskclock",           // Clock
            "com.google.android.calculator",          // Calculator
            "com.android.chrome",                     // Chrome
            "com.google.android.youtube",             // YouTube
            "com.google.android.apps.maps",           // Maps
            "com.google.android.gm",                  // Gmail
            "com.google.android.apps.photos",         // Photos
            "com.google.android.inputmethod.latin",   // Gboard
            "com.google.android.keep",                // Keep Notes
            "com.google.android.apps.docs",           // Drive
            "com.google.android.apps.tachyon",        // Duo / Meet
            "com.google.android.apps.walletnfcrel",   // Wallet
            "com.google.android.apps.wellbeing",      // Digital Wellbeing

            // --- Pixel Specific ---
            "com.google.android.apps.nexuslauncher",  // Pixel Launcher
            "com.google.android.apps.pixelmigrate",   // Data Transfer
            "com.google.android.apps.tips",           // Pixel Tips
            "com.google.android.apps.wallpaper",      // Wallpapers
            "com.google.android.GoogleCamera",        // Camera
            "com.google.pixel.livewallpaper",         // Live Wallpaper
            "com.google.android.apps.safetyhub",      // Personal Safety
            "com.google.android.apps.recorder",       // Recorder

            // --- Android System Components ---
            "com.android.systemui",                   // System UI
            "com.android.settings",                   // Settings
            "com.android.phone",                      // Phone Services
            "com.android.providers.telephony",        // Telephony Provider
            "com.android.providers.media",            // Media Storage
            "com.android.providers.contacts",         // Contacts Storage
            "com.android.providers.userdictionary",   // User Dictionary
            "com.android.providers.calendar",         // Calendar Storage
            "com.android.providers.settings",         // Settings Storage
            "com.android.shell",                      // Shell
            "android",                                // Android System
            "com.android.bluetooth",                  // Bluetooth
            "com.android.nfc",                        // NFC
            "com.android.location.fused",             // Fused Location
            "com.android.server.telecom"              // Telecom Server
        )

        fun isWhitelisted(packageName: String, isSystemApp: Boolean): Boolean {
            // 1. Check exact matches in the manual list
            if (safePackages.contains(packageName)) return true

            // 2. Check for System Apps with known safe namespaces
            // Only apply wildcard matching if it is actually a system app to prevent
            // malicious user apps from mimicking these names (e.g. com.google.android.malware)
            if (isSystemApp) {
                if (packageName.startsWith("com.google.android.")) return true
                if (packageName.startsWith("com.android.")) return true
                if (packageName.startsWith("com.sec.android.")) return true // Samsung System
                if (packageName.startsWith("com.samsung.android.")) return true // Samsung System
                if (packageName.startsWith("com.verizon.")) return true // Verizon
                if (packageName.startsWith("com.vzw.")) return true // Verizon
                if (packageName.startsWith("com.att.")) return true // AT&T
                if (packageName.startsWith("com.tmobile.")) return true // T-Mobile
                if (packageName.startsWith("com.sprint.")) return true // Sprint
                if (packageName.startsWith("com.huawei.")) return true // Huawei
                if (packageName.startsWith("com.xiaomi.")) return true // Xiaomi
                if (packageName.startsWith("com.oppo.")) return true // Oppo
                if (packageName.startsWith("com.oneplus.")) return true // OnePlus
                if (packageName.startsWith("com.motorola.")) return true // Motorola
                if (packageName.startsWith("com.lge.")) return true // LG
                
                // --- UK Carriers ---
                if (packageName.startsWith("com.vodafone.")) return true // Vodafone
                if (packageName.startsWith("uk.co.vodafone.")) return true // Vodafone UK
                if (packageName.startsWith("com.o2.")) return true // O2
                if (packageName.startsWith("uk.co.o2.")) return true // O2 UK
                if (packageName.startsWith("com.ee.")) return true // EE
                if (packageName.startsWith("uk.co.ee.")) return true // EE UK
                if (packageName.startsWith("com.three.")) return true // Three
                if (packageName.startsWith("com.hutchison3g.")) return true // Three (Parent)
                if (packageName.startsWith("com.tescomobile.")) return true // Tesco Mobile
                if (packageName.startsWith("com.virginmobile.")) return true // Virgin Mobile
                if (packageName.startsWith("com.sky.mobile.")) return true // Sky Mobile
                if (packageName.startsWith("com.giffgaff.")) return true // Giffgaff
                if (packageName.startsWith("com.talktalk.")) return true // TalkTalk
                if (packageName.startsWith("com.bt.")) return true // BT Mobile
                if (packageName.startsWith("com.plusnet.")) return true // Plusnet
            }

            return false
        }
    }
}
