# AntiStalker - Android Stalkerware Detection

AntiStalker is a native Android antivirus application designed to identify and mitigate stalkerware—malicious apps that covertly monitor user activity, location, and communications.

## Project Overview

This tool scans installed applications on an Android device and analyzes them for "stalkerware-like" behavior using a heuristic engine. It flags apps that exhibit suspicious combinations of permissions, attempt to hide their presence, or abuse powerful system features.

## How It Works

The app uses a **Heuristic Analysis Engine** to score apps based on multiple risk factors. It does not rely solely on a database of known malware signatures (though it supports one), but rather on *behavioral analysis*.

### Key Detection Mechanisms

1.  **Permission Analysis (`PermissionAnalyzer.kt`)**
    *   Scans for dangerous permission combinations that indicate spying.
    *   **Examples:**
        *   `ACCESS_FINE_LOCATION` + `READ_SMS` -> "Tracks Location and SMS"
        *   `RECORD_AUDIO` + `ACCESS_BACKGROUND_LOCATION` -> "Spying (Audio + Location)"
        *   `SYSTEM_ALERT_WINDOW` + `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` -> "Persistent Hidden Overlay"
        *   `PACKAGE_USAGE_STATS` -> "Monitors Other App Usage"

2.  **Hidden App Detection (`HiddenAppDetector.kt`)**
    *   Identifies apps that do not have a launcher icon (a common tactic to hide from the user).
    *   Checks if a "hidden" app also requests dangerous permissions (High Risk).
    *   Checks against a list of known stalkerware package names.

3.  **Accessibility Service Abuse (`AccessibilityDetector.kt`)**
    *   Detects apps that request `BIND_ACCESSIBILITY_SERVICE`.
    *   This service is often abused by stalkerware to read screen content (Keylogging, reading WhatsApp messages) without root access.

4.  **Admin Privilege Check (`AdminAccessDetector.kt`)**
    *   Flags apps that have granted themselves Device Admin rights, making them difficult to uninstall.

5.  **Smart Whitelist System (`Whitelist.kt`)**
    *   To prevent false positives, the engine whitelists known safe system applications.
    *   **Logic**:
        *   **Manual List**: Includes known package names like `com.google.android.gms`, `com.google.android.GoogleCamera`, `com.google.android.apps.safetyhub`.
        *   **System App Verification**: Checks if an app is a pre-installed System App (`isSystemApp`) AND matches a trusted namespace (`com.google.android.`, `com.android.`, `com.samsung.`).
        *   **Security Note**: This logic prevents malware from simply naming itself `com.google.android.malware` to bypass detection, as user-installed apps will fail the `isSystemApp` check.

### Risk Levels

Apps are categorized into four levels:
*   **HIGH**: Strong indicators of stalkerware (e.g., Hidden app + Accessibility Service, or known signature).
*   **MEDIUM**: Suspicious behavior (e.g., Excessive dangerous permissions without being a system app).
*   **LOW**: Minor concerns.
*   **SAFE**: No suspicious indicators found or app is whitelisted.

## Getting Started

### Prerequisites
*   Android Studio (Latest version recommended)
*   Android SDK (API Level 34 / Android 14)
*   Java Development Kit (JDK) 17+

### Installation
1.  Open the project in **Android Studio**.
2.  Allow Gradle to sync dependencies.
3.  Connect an Android device (with USB Debugging enabled) or start an Emulator.
4.  Run the application (`Shift + F10`).

### Usage
1.  Launch the **AntiStalker** app.
2.  Grant the "Query All Packages" permission if prompted (automatic on most installs).
3.  Tap the large **"Start Scan"** button.
4.  Review the list of flagged applications.
    *   **Red (HIGH)**: Investigate immediately. If you do not recognize the app, consider uninstalling it.
    *   **Orange (MEDIUM)**: Review permissions.
5.  If no threats are found, a "Safe" message will appear.

## Disclaimer

This tool is a research prototype intended for educational and defensive purposes. It provides heuristics to assist in detection but cannot guarantee 100% accuracy. Always verify findings before removing essential system applications.
