package com.example.bookphoria.data.remote.pagingsources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookphoria.data.local.entities.BookData
import com.example.bookphoria.data.remote.api.BookApiService
import com.example.bookphoria.data.repository.BookRepository

class BookSearchPagingSource(
    private val apiService: BookApiService,
    private val query: String,
    private val token: String,
    private val bookRepository: BookRepository
) : PagingSource<Int, BookData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookData> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val offset = (page - 1) * pageSize

            val books = try {
                bookRepository.getBooksByQuery(query, pageSize, offset)
            } catch (e: Exception) {
                Log.e("Paging", "Error fetching books from API: ${e.message}")
                emptyList()
            }

                try {
                    val response = apiService.getBooksByQuery("Bearer $token", query, pageSize, page)
                    val apiBooks = response.data
                    apiBooks.forEach { book ->
                        try {
                            bookRepository.insertBook(book)
                            Log.d("Paging", "Book saved to local: ${book.title}")
                        } catch (e: Exception) {
                            Log.e("Paging", "Failed to save book ${book.title} to local database")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Paging", "Failed to fetch books from API: ${e.message}")
                }

            LoadResult.Page(
                data = books,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (books.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("Paging", "Error loading books")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, BookData>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}