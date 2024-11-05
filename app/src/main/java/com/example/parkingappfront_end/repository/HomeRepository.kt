package com.example.parkingappfront_end.repository

class HomeRepository(private val apiService: BooksApiService) {

    suspend fun addBook(book: Book) {
        try {
            apiService.insertBook(book)
        } catch (e: Exception) {
            println("Errore durante l'aggiunta del libro: ${e.message}")
        }
    }

    suspend fun getBook(bookId: Long): Book? {
        return try {
            apiService.getBook(bookId)
        } catch (e: Exception) {
            println("Errore durante il recupero del libro: ${e.message}")
            null
        }
    }

    suspend fun deleteBook(bookId: Long) {
        try {

        } catch (e: Exception) {
            println("Errore durante la cancellazione del libro: ${e.message}")
        }
    }
}
