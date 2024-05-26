package com.android.imagesearch.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.android.imagesearch.ui.util.Constants
import com.android.imagesearch.api.KaKaoSearchApi
import com.android.imagesearch.data.SearchListType
import com.android.imagesearch.data.ImageDocument
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.data.SearchResponse
import com.android.imagesearch.data.SearchUiState
import com.android.imagesearch.data.VideoDocument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit과 로컬 저장소 SharedPreferences를 사용하여 내 보관함 관련 기능을 처리
 */
@Singleton
class ImageSearchRepositoryImpl @Inject constructor(
    private val api: KaKaoSearchApi,
    private val sharedPreferences: SharedPreferences
) : ImageSearchRepository {
    override suspend fun searchImage(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ): SearchResponse<ImageDocument> {
        return api.searchImage(query, sort, page, size)
    }

    override suspend fun searchVideo(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ): SearchResponse<VideoDocument> {
        return api.searchVideo(query, sort, page, size)
    }

    /**
     * 이미지 검색 화면에서 이미지를 클릭하면 보관함에 저장하기 위한 함수
     * id를 비교해서 존재하지 않을 경우에만 아이템을 추가한다.
     */
    override suspend fun saveStorageItem(searchModel: SearchModel) {
        val favoriteItems = getPrefsStorageItems().toMutableList()
        val findItem = favoriteItems.find { it.thumbnailUrl == searchModel.thumbnailUrl }

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
        favoriteItems.removeAll { it.thumbnailUrl == searchModel.thumbnailUrl }
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
        imagePage: Int,
        videoPage: Int
    ): Pair<SearchUiState, SearchUiState> = coroutineScope {
        val imageDeferred = async {
            try {
                val response = searchImage(query = query, page = imagePage)
                SearchUiState(list = response.documents?.map {
                    SearchModel(
                        thumbnailUrl = it.thumbnailUrl,
                        siteName = it.displaySiteName,
                        datetime = it.dateTime,
                        itemType = SearchListType.IMAGE
                    )
                } ?: emptyList())
            } catch (e: Exception) {
                throw e
            }
        }

        val videoDeferred = async {
            try {
                val response = searchVideo(query = query, page = videoPage)
                SearchUiState(list = response.documents?.map {
                    SearchModel(
                        thumbnailUrl = it.thumbnailUrl,
                        siteName = it.title,
                        datetime = it.dateTime,
                        itemType = SearchListType.VIDEO
                    )
                } ?: emptyList())
            } catch (e: Exception) {
                throw e
            }
        }

        Pair(imageDeferred.await(), videoDeferred.await())
    }

    private fun getPrefsStorageItems(): List<SearchModel> {
        val jsonString = sharedPreferences.getString(Constants.STORAGE_ITEMS, "")
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
        sharedPreferences.edit().putString(Constants.STORAGE_ITEMS, jsonString).apply()
    }

    /**
     * 검색 키워드 저장
     */
    override suspend fun saveSearchData(searchWord: String) {
        sharedPreferences.edit {
            putString(Constants.SEARCH_WORD, searchWord)
        }
    }

    /**
     * 검색 키워드 불러오기
     */
    override suspend fun loadSearchData(): String? =
        sharedPreferences.getString(Constants.SEARCH_WORD, "")

}