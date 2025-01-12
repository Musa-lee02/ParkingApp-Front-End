package com.example.parkingappfront_end.model.domain

import android.content.Context
import android.widget.TextView
import com.example.parkingappfront_end.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val date = LocalDate.ofEpochDay(e.x.toLong())
            val value = e.y
            tvContent.text = "${date.format(DateTimeFormatter.ofPattern("dd/MM"))} \n€%.2f".format(value)
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }
}