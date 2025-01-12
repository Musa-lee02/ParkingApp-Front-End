package com.example.parkingappfront_end.model.domain

import com.example.parkingappfront_end.ui.ownerSide.FilterType
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateAxisValueFormatter(
    private val dates: List<LocalDate>,
    private val filterType: FilterType
) : ValueFormatter() {
    private val dayFormatter = DateTimeFormatter.ofPattern("dd/MM")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

    override fun getFormattedValue(value: Float): String {
        val date = LocalDate.ofEpochDay(value.toLong())
        return when (filterType) {
            FilterType.DAILY -> date.format(dayFormatter)
            FilterType.MONTHLY -> date.format(monthFormatter)
        }
    }
}
