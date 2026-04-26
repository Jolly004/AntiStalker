package com.example.antistalker

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScanResultAdapter(
    private var results: List<ScanResult>,
    @Suppress("unused") // Kept for source compat with existing call sites. Both card and button now route to onUninstallClick.
    private val onItemClick: (ScanResult) -> Unit,
    private val onUninstallClick: (ScanResult) -> Unit
) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView = view.findViewById(R.id.tvAppName)
        val packageName: TextView = view.findViewById(R.id.tvPackageName)
        val appIcon: ImageView = view.findViewById(R.id.ivAppIcon)
        val riskLevel: TextView = view.findViewById(R.id.tvRiskLevel)
        val riskFactors: TextView = view.findViewById(R.id.tvRiskFactors)
        val btnUninstall: Button = view.findViewById(R.id.btnUninstall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scan_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.appName.text = result.appInfo.appName
        holder.packageName.text = result.appInfo.packageName
        holder.appIcon.setImageDrawable(result.appInfo.icon)
        
        holder.riskLevel.text = result.riskLevel.name
        val color = when (result.riskLevel) {
            RiskLevel.CRITICAL -> Color.parseColor("#FF5252") // Red
            RiskLevel.HIGH -> Color.parseColor("#FF5252")
            RiskLevel.MEDIUM -> Color.parseColor("#FFA726") // Orange
            RiskLevel.LOW -> Color.parseColor("#FFEE58") // Yellow
            RiskLevel.SAFE -> Color.parseColor("#66BB6A") // Green
        }
        holder.riskLevel.setTextColor(color)
        
        // Update background tint for badge effect if using a shape drawable with tint support
        // For now, just text color is updated as per previous logic, but the layout uses a semi-transparent bg.

        holder.riskFactors.text = result.riskFactors.joinToString("\n") { "- $it" }

        // Click listeners — belt-and-braces approach.
        // On some Samsung One UI builds the inner MaterialButton's click listener
        // never fires because the parent MaterialCardView is consuming the touch
        // sequence. Rather than chase that platform quirk, we wire BOTH the card
        // and the button to the SAME uninstall callback.