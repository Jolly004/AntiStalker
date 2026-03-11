package com.example.antistalker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ScanHistoryItem(
    val timestamp: Long,
    val riskCount: Int,
    val criticalCount: Int,
    val highCount: Int,
    val mediumCount: Int,
    val lowCount: Int
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

class ScanHistoryManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("scan_history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val historyKey = "scan_history_list"

    fun saveScanResult(results: List<ScanResult>) {
        val currentHistory = getHistory().toMutableList()
        
        val criticalCount = results.count { it.riskLevel == RiskLevel.CRITICAL }
        val highCount = results.count { it.riskLevel == RiskLevel.HIGH }
        val mediumCount = results.count { it.riskLevel == RiskLevel.MEDIUM }
        val lowCount = results.count { it.riskLevel == RiskLevel.LOW }
        val riskCount = results.size

        val newItem = ScanHistoryItem(
            timestamp = System.currentTimeMillis(),
            riskCount = riskCount,
            criticalCount = criticalCount,
            highCount = highCount,
            mediumCount = mediumCount,
            lowCount = lowCount
        )

        currentHistory.add(0, newItem) // Add to top
        
        // Limit history to last 20 scans
        if (currentHistory.size > 20) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        val json = gson.toJson(currentHistory)
        prefs.edit().putString(historyKey, json).apply()
        
        // Also save last scan timestamp for MainActivity
        prefs.edit().putLong("last_scan_timestamp", newItem.timestamp).apply()
    }

    fun getHistory(): List<ScanHistoryItem> {
        val json = prefs.getString(historyKey, null) ?: return emptyList()
        val type = object : TypeToken<List<ScanHistoryItem>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getLastScanTimestamp(): Long {
        return prefs.getLong("last_scan_timestamp", 0)
    }
    
    fun clearHistory() {
        prefs.edit().remove(historyKey).apply()
    }
}
