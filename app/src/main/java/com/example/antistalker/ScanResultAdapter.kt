package com.example.antistalker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScanResultAdapter(
    private var results: List<ScanResult>,
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
        when (result.riskLevel) {
            RiskLevel.CRITICAL -> holder.riskLevel.setTextColor(Color.parseColor("#B71C1C")) // Dark Red
            RiskLevel.HIGH -> holder.riskLevel.setTextColor(Color.RED)
            RiskLevel.MEDIUM -> holder.riskLevel.setTextColor(Color.parseColor("#FFA500")) // Orange
            RiskLevel.LOW -> holder.riskLevel.setTextColor(Color.parseColor("#FFD700")) // Gold
            RiskLevel.SAFE -> holder.riskLevel.setTextColor(Color.GREEN)
        }

        holder.riskFactors.text = result.riskFactors.joinToString("\n") { "- $it" }

        // Click listeners
        holder.itemView.setOnClickListener { onItemClick(result) }
        holder.btnUninstall.setOnClickListener { onUninstallClick(result) }
    }

    override fun getItemCount() = results.size

    fun updateData(newResults: List<ScanResult>) {
        results = newResults
        notifyDataSetChanged()
    }
}
