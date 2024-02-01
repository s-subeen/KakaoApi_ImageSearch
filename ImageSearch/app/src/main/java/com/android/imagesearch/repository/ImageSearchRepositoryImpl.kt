package com.android.imagesearch.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.android.imagesearch.Constants
import com.android.imagesearch.data.SearchListType
import com.android.imagesearch.api.NetWorkClient
import com.android.imagesearch.data.ImageDocument
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.data.SearchResponse
import com.android.imagesearch.data.VideoDocument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Retrofit과 로컬 저장소 SharedPreferences를 사용하여 내 보관함 관련 기능을 처리
 */
class ImageSearchRepositoryImpl(context: Context) : ImageSearchRepository {
    override suspend fun searchImage(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ): SearchResponse<ImageDocument> {
        return NetWorkClient.ImageNetWork.searchImage(query, sort, page, size)
    }

    override suspend fun searchVideo(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ): SearchResponse<VideoDocument> {
        return NetWorkClient.ImageNetWork.searchVideo(query, sort, page, size)
    }

    private val pref: SharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_NAME, 0)

    /**
     * 이미지 검색 화면에서 이미지를 클릭하면 보관함에 저장하기 위한 함수
     * id를 비교해서 존재하지 않을 경우에만 아이템을 추가한다.
     */
    override suspend fun saveStorageItem(searchModel: SearchModel) {
        val favoriteItems = getPrefsStorageItems().toMutableList()
        val findItem = favoriteItems.find { it.id == searchModel.id }

        if (findItem == null) {
            favoriteItems.add(searchModel)
            savePrefsStorageItems(favoriteItems)
        }
    }

    /**
     * 보관함에 저장된 이미지를 삭제하기 위한 함수
     * 해당 아이템이 보관함에 존재하면 아이템을 삭제한다.
     */
    override suspend fun removeStorageItem(searchModel: SearchModel) {
        val favoriteItems = getPrefsStorageItems().toMutableList()
        favoriteItems.removeAll { it.id == searchModel.id }
        savePrefsStorageItems(favoriteItems)
    }

    /**
     * 보관함에 저장되어 있는 아이템을 리스트 목록으로 가져온다.
     */
    override suspend fun getStorageItems(): List<SearchModel> {
        return getPrefsStorageItems()
    }

    override suspend fun searchCombinedResults(
        query: String,
        imageSize: Int,
        videoSize: Int
    ): Pair<List<SearchModel>, List<SearchModel>> = coroutineScope {
        val imageDeferred = async {
            searchImage(query = query, page = imageSize).documents?.map {
                SearchModel(
                    thumbnailUrl = it.thumbnailUrl,
                    siteName = it.displaySiteName,
                    datetime = it.dateTime,
                    itemType = SearchListType.IMAGE
                )
            } ?: emptyList()
        }

        val videoDeferred = async {
            searchVideo(query = query, page = videoSize).documents?.map {
                SearchModel(
                    thumbnailUrl = it.thumbnailUrl,
                    siteName = it.title,
                    datetime = it.dateTime,
                    itemType = SearchListType.VIDEO
                )
            } ?: emptyList()
        }

        try {
            val imageResponse = imageDeferred.await()
            val videoResponse = videoDeferred.await()
            Pair(imageResponse, videoResponse)
        } catch (e: Exception) {
            throw e
        }
    }


    private fun getPrefsStorageItems(): List<SearchModel> {
        val jsonString = pref.getString(Constants.STORAGE_ITEMS, "")
        return if (jsonString.isNullOrEmpty()) {
            emptyList()
        } else {
            /**
             * Gson()을 사용하여 Json 문자열을 SearchModel 객체로 변환
             */
            Gson().fromJson(jsonString, object : TypeToken<List<SearchModel>>() {}.type)
        }
    }

    /**
     * SearchModel 객체 아이템을 Json 문자열로 변환한 후 저장
     */
    private fun savePrefsStorageItems(items: List<SearchModel>) {
        val jsonString = Gson().toJson(items)
        pref.edit().putString(Constants.STORAGE_ITEMS, jsonString).apply()
    }

    /**
     * 검색 키워드 저장
     */
    override suspend fun saveSearchData(searchWord: String) {
        pref.edit {
            putString(Constants.SEARCH_WORD, searchWord)
        }
    }

    /**
     * 검색 키워드 불러오기
     */
    override suspend fun loadSearchData(): String? =
        pref.getString(Constants.SEARCH_WORD, "")

}