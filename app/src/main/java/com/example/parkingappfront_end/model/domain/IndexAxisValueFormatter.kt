package com.example.parkingappfront_end.model.domain

import com.github.mikephil.charting.formatter.ValueFormatter

class IndexAxisValueFormatter(private val labels: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index in labels.indices) labels[index] else ""
    }
}
