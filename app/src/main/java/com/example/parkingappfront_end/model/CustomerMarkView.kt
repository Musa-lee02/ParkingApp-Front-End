package com.example.parkingappfront_end.model

import android.content.Context
import android.widget.TextView
import com.example.parkingappfront_end.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    // Inizializza tvContent nella funzione init
    private lateinit var tvContent: TextView

    init {
        // Inizializza la TextView dopo che la view è stata inflata
        tvContent = findViewById(R.id.tvContent)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            tvContent.text = "€${it.y.toInt()}"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}