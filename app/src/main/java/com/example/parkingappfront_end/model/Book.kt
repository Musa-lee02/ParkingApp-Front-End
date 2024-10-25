package com.example.parkingappfront_end.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Book(
    val id: Long,
    val title: String,
    val author: String,

    @SerializedName("isbn")
    val ISBN: String, // Annotazione per mappare la proprietà "isbn"

    val pages: Int,
    val edition: String,
    val publisher: String,
    val age: Int,
    val publishDate: LocalDate, // Annotazione per mappare la proprietà "publish_date"
    val weight: Double,
    val price: Double,
    val stock: Int,
    //val image: ByteArray,

)
