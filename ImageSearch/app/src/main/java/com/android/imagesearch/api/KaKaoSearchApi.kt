package com.android.imagesearch.api


import com.android.imagesearch.Constants
import com.android.imagesearch.data.ImageDocument
import com.android.imagesearch.data.SearchResponse
import com.android.imagesearch.data.VideoDocument
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface KaKaoSearchApi {
    @Headers("Authorization: ${Constants.AUTH_HEADER}")
    @GET("v2/search/image")
    suspend fun searchImage(
        @Query("query") query: String, // 검색을 원하는 질의어
        @Query("sort") sort: String, // 결과 문서 정렬 방식, accuracy(정확도순) 또는 recency(최신순), 기본 값 accuracy
        @Query("page") page: Int, // 결과 페이지 번호, 1~50 사이의 값, 기본 값 1
        @Query("size") size: Int // 한 페이지에 보여질 문서 수, 1~80 사이의 값, 기본 값 80
    ): SearchResponse<ImageDocument>

    @Headers("Authorization: ${Constants.AUTH_HEADER}")
    @GET("v2/search/vclip")
    suspend fun searchVideo(
        @Query("query") query: String, // 검색을 원하는 질의어
        @Query("sort") sort: String, // 결과 문서 정렬 방식, accuracy(정확도순) 또는 recency(최신순), 기본 값 accuracy
        @Query("page") page: Int, // 결과 페이지 번호, 1~15 사이의 값
        @Query("size") size: Int // 한 페이지에 보여질 문서 수, 1~30 사이의 값, 기본 값 15
    ): SearchResponse<VideoDocument>

}