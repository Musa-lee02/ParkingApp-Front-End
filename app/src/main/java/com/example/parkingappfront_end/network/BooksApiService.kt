package com.example.parkingappfront_end.network

import com.example.parkingappfront_end.model.Book
import retrofit2.Response

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDate

interface BooksApiService {

    @POST("books/add")
    suspend fun insertBook(insertBook: Book)

    @GET("books/get/{idBook}")
    suspend fun getBook(@Path("idBook") idBook: Long) : Book

    @GET("books/getAll")
    suspend fun getAllBooks() : Response<List<Book>>

}