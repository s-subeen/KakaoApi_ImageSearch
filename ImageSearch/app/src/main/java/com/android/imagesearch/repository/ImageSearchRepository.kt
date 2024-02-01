package com.android.imagesearch.repository

import com.android.imagesearch.Constants.Companion.MAX_SIZE_IMAGE
import com.android.imagesearch.Constants.Companion.MAX_SIZE_VIDEO
import com.android.imagesearch.Constants.Companion.SORT_TYPE
import com.android.imagesearch.data.ImageDocument
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.data.SearchResponse
import com.android.imagesearch.data.SearchUiState
import com.android.imagesearch.data.VideoDocument
import retrofit2.http.Query

interface ImageSearchRepository {

    suspend fun searchImage(
        query: String,
        sort: String = SORT_TYPE,
        page: Int,
        size: Int = MAX_SIZE_IMAGE
    ): SearchResponse<ImageDocument>

    suspend fun searchVideo(
        @Query("query") query: String,
        @Query("sort") sort: String = SORT_TYPE,
        @Query("page") page: Int,
        @Query("size") size: Int = MAX_SIZE_VIDEO
    ): SearchResponse<VideoDocument>


    suspend fun saveStorageItem(searchModel: SearchModel)

    suspend fun removeStorageItem(searchModel: SearchModel)

    suspend fun getStorageItems(): List<SearchModel>

    suspend fun searchCombinedResults(
        query: String,
        imagePage: Int,
        videoPage: Int
    ): Pair<SearchUiState, SearchUiState>

    suspend fun saveSearchData(searchWord: String)

    suspend fun loadSearchData(): String?
}