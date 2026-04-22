# AntiStalker: Android Stalkerware Detection

AntiStalker is a native Android antivirus app built to find and remove stalkerware. These are malicious applications that secretly monitor your location, communications, and everyday activity.

## Project Overview

Instead of just relying on a basic list of known malware, AntiStalker uses behavioral analysis. The app's heuristic engine scans your device and scores installed applications based on how they act. If an app requests highly suspicious permission combinations, tries to hide from the app drawer, or abuses powerful system features, AntiStalker flags it for you.

### What It Looks For

1. **Permission Abuse (`PermissionAnalyzer.kt`)**
   The app looks for dangerous combinations of permissions that point to spying. For example:
   * `ACCESS_FINE_LOCATION` and `READ_SMS` together suggest the app is tracking your location and reading your texts.
   * `RECORD_AUDIO` and `ACCESS_BACKGROUND_LOCATION` indicate audio and location spying.
   * `SYSTEM_ALERT_WINDOW` paired with `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` is a classic sign of a persistent, hidden overlay.
   * `PACKAGE_USAGE_STATS` means the app is monitoring what other apps you use.

2. **Hidden Apps (`HiddenAppDetector.kt`)**
   Stalkerware loves to hide. This detector finds apps that don't have a visible launcher icon. If a hidden app is also asking for dangerous permissions, the risk score shoots up. It also checks against a built-in list of known stalkerware package names.

3. **Accessibility Service Abuse (`AccessibilityDetector.kt`)**
   Stalkerware often exploits `BIND_ACCESSIBILITY_SERVICE` to read what is on your screen, like WhatsApp messages, or to log your keystrokes without needing root access. AntiStalker highlights any app requesting this capability.

4. **Device Admin Privileges (`AdminAccessDetector.kt`)**
   Some malicious apps make themselves Device Administrators so you cannot easily uninstall them. The scanner flags anything trying to do this.

5. **Smart Whitelisting (`Whitelist.kt`)**
   To stop the app from constantly flagging standard system functions, we use a smart whitelist. 
   * It includes known safe packages like `com.google.android.gms` or your default camera app.
   * It verifies system apps by checking if they are pre-installed AND match a trusted namespace like `com.google.android.` or `com.samsung.`.
   * This stops malware from simply tricking the scanner by naming itself something like `com.google.android.malware`, because a user-installed app will fail the pre-installed system check.

### Risk Levels

After scanning, apps are grouped into four categories:
* **HIGH**: Strong signs of stalkerware, like a hidden app using accessibility services, or a match with a known stalkerware signature.
* **MEDIUM**: Suspicious behavior, such as a regular user app asking for too many dangerous permissions.
* **LOW**: Minor concerns worth a quick glance.
* **SAFE**: The app is either whitelisted or shows no sketchy behavior at all.

## Getting Started

### What You Need
* Android Studio (latest version is best)
* Android SDK (API Level 34 / Android 14)
* Java Development Kit (JDK) 17 or higher

### Installation
1. Open the project in Android Studio.
2. Let Gradle sync all the dependencies.
3. Plug in an Android device with USB Debugging enabled, or boot up an emulator.
4. Hit Run (`Shift + F10`).

### How to Use It
1. Open the AntiStalker app.
2. If it asks for the "Query All Packages" permission, go ahead and grant it (though this happens automatically on most setups).
3. Tap the big **Start Scan** button.
4. Look through the results:
   * **Red (HIGH)**: Check these out immediately. If you do not recognize the app, you should probably uninstall it.
   * **Orange (MEDIUM)**: Double-check what permissions these apps are using.
5. If everything looks good, you will just see a "Safe" message.

## Disclaimer

This is a research prototype built for educational and defensive use. While the heuristics are designed to help you spot threats, it isn't guaranteed to be 100% accurate. Please make sure you actually know what an app is before you go deleting essential system files.
