package com.example.parkingappfront_end.model

import java.time.LocalDateTime

data class SearchParams(
    val city: String,
    val startDate: LocalDateTime,
    val endDate:  LocalDateTime
)