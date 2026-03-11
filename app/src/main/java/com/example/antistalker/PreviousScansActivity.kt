package com.example.antistalker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreviousScansActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var tvNoHistory: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var historyManager: ScanHistoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_scans)

        rvHistory = findViewById(R.id.rvHistory)
        tvNoHistory = findViewById(R.id.tvNoHistory)
        btnBack = findViewById(R.id.btnBack)
        historyManager = ScanHistoryManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val history = historyManager.getHistory()
        
        if (history.isEmpty()) {
            tvNoHistory.visibility = View.VISIBLE
            rvHistory.visibility = View.GONE
        } else {
            tvNoHistory.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE
            rvHistory.layoutManager = LinearLayoutManager(this)
            rvHistory.adapter = HistoryAdapter(history)
        }
    }

    class HistoryAdapter(private val history: List<ScanHistoryItem>) :
        RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvDate: TextView = view.findViewById(R.id.tvDate)
            val tvRiskCount: TextView = view.findViewById(R.id.tvRiskCount)
            val tvCriticalCount: TextView = view.findViewById(R.id.tvCriticalCount)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_scan_history, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = history[position]
            holder.tvDate.text = item.getFormattedDate()
            holder.tvRiskCount.text = "Risks Found: ${item.riskCount}"
            holder.tvCriticalCount.text = "Critical: ${item.criticalCount}"
        }

        override fun getItemCount() = history.size
    }
}
