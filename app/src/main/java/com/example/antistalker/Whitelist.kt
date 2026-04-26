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
            "com.android.server.telecom",             // Telecom Server

            // --- Samsung specific stragglers (often pre-installed but not under com.samsung.android.*) ---
            "com.osp.app.signin",                     // Samsung Account
            "com.wssyncmldm",                         // Samsung Software Update
            "com.wsomacp",                            // Samsung Configuration Message
            "com.samsung.knox.securefolder",          // Samsung Secure Folder
            "com.samsung.android.spay",               // Samsung Pay / Wallet
            "com.samsung.android.spayfw",             // Samsung Pay Framework

            // --- Google legacy / discontinued but still installable ---
            "com.google.android.music",               // Google Play Music (discontinued, still on some devices)
            "com.google.android.videos",              // Google Play Movies (discontinued)
            "com.google.android.play.games",          // Play Games
            "com.google.android.apps.subscriptions.red", // Google One
            "com.google.android.apps.fitness"         // Google Fit
        )

        // Curated allowlist of well-known consumer apps with massive install bases that
        // legitimately request scary-looking permission combinations (location + audio,
        // overlay + battery exemption, etc.).
        //
        // IMPORTANT: This is checked AFTER the known-stalkerware signature match in the
        // engine, so a stalkerware app that spoofs one of these package names still gets
        // caught by the signature layer rather than being silently allowed.
        private val popularApps = setOf(
            // Messaging / calls
            "com.whatsapp",                       // WhatsApp
            "com.whatsapp.w4b",                   // WhatsApp Business
            "com.skype.raider",                   // Skype
            "com.skype.m2",                       // Skype Lite
            "org.telegram.messenger",             // Telegram
            "org.telegram.messenger.web",         // Telegram (web build)
            "org.thoughtcrime.securesms",         // Signal
            "com.discord",                        // Discord
            "com.facebook.orca",                  // Messenger
            "com.facebook.mlite",                 // Messenger Lite
            "com.viber.voip",                     // Viber
            "jp.naver.line.android",              // LINE
            "com.kakao.talk",                     // KakaoTalk
            "com.tencent.mm",                     // WeChat
            "us.zoom.videomeetings",              // Zoom
            "com.microsoft.teams",                // Microsoft Teams
            "com.cisco.webex.meetings",           // Webex
            "com.google.android.apps.tachyon",    // Google Meet / Duo (also in safePackages)

            // Social / media
            "com.facebook.katana",                // Facebook
            "com.facebook.lite",                  // Facebook Lite
            "com.instagram.android",              // Instagram
            "com.instagram.lite",                 // I