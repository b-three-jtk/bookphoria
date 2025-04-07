package com.example.bookphoria.data.remote.pagingsources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookphoria.data.remote.api.BookApiService
import com.example.bookphoria.data.remote.responses.BookNetworkModel

class BookSearchPagingSource(
    private val apiService: BookApiService,
    private val query: String,
    private val token: String
) : PagingSource<Int, BookNetworkModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookNetworkModel> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val response = apiService.getBooksByQuery("Bearer $token", query, pageSize, page)
            val books = response.data
            LoadResult.Page(
                data = books,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (books.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

        }

    override fun getRefreshKey(state: PagingState<Int, BookNetworkModel>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
