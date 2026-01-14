package com.example.antistalker

enum class RiskLevel {
    CRITICAL, HIGH, MEDIUM, LOW, SAFE
}

data class ScanResult(
    val appInfo: AppInfo,
    val riskLevel: RiskLevel,
    val riskFactors: List<String>
)
