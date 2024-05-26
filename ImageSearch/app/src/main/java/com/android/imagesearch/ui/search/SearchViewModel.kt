package com.android.imagesearch.ui.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.imagesearch.ui.util.Constants.Companion.MAX_PAGE_COUNT_IMAGE
import com.android.imagesearch.ui.util.Constants.Companion.MAX_PAGE_COUNT_VIDEO
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.data.SearchPageCountUiState
import com.android.imagesearch.data.SearchUiState
import com.android.imagesearch.repository.ImageSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val imageSearchRepository: ImageSearchRepository
) : ViewModel() {
    private val _searchResult = MutableStateFlow(SearchUiState.init())
    val searchResult: StateFlow<SearchUiState> = _searchResult

    private val _pageCounts = mutableStateOf(SearchPageCountUiState.init())
    val pageCounts: State<SearchPageCountUiState> = _pageCounts

    init {
        getStorageSearchWord()
    }

    fun saveStorageSearchWord(query: String) {
        viewModelScope.launch {
            imageSearchRepository.saveSearchData(query)
            // 검색어를 저장하고 searchResult를 업데이트
            _searchResult.update { currentState ->
                currentState.copy(keyword = query)
            }
        }
    }

    private fun getStorageSearchWord() {
        viewModelScope.launch {
            val query = imageSearchRepository.loadSearchData().orEmpty()
            // 검색어를 가져와 searchResult를 업데이트
            _searchResult.update { currentState ->
                currentState.copy(keyword = query)
            }
        }
    }

    fun searchCombinedResults(query: String) {
        viewModelScope.launch {
            try {
                val pageCounts = _pageCounts.value

                val (imageResponse, videoResponse) =
                    imageSearchRepository.searchCombinedResults(
                        query = query,
                        imagePage = pageCounts.imagePageCount,
                        videoPage = pageCounts.videoPageCount
                    )

                _searchResult.update {
                    SearchUiState(
                        list = (imageResponse.list + videoResponse.list).sortedByDescending { it.datetime },
                        keyword = query // 검색 결과와 함께 검색어도 설정
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reloadStorageItems() {
        viewModelScope.launch {
            val storageItems = imageSearchRepository.getStorageItems()

            _searchResult.update { currentState ->
                currentState.copy(
                    list = currentState.list.map { currentItem ->
                        currentItem.copy(isSaved = storageItems.any { it.thumbnailUrl == currentItem.thumbnailUrl })
                    }
                )
            }
        }
    }

    fun updateStorageItem(searchModel: SearchModel) {
        val updatedItem = searchModel.copy(isSaved = !searchModel.isSaved)

        viewModelScope.launch {
            if (updatedItem.isSaved) {
                imageSearchRepository.saveStorageItem(updatedItem)
            } else {
                imageSearchRepository.removeStorageItem(updatedItem)
            }
            _searchResult.update { currentState ->
                currentState.copy(
                    list = currentState.list.map {
                        if (it.thumbnailUrl == updatedItem.thumbnailUrl) updatedItem else it
                    }
                )
            }
        }
    }

    fun resetPageCount() {
        _pageCounts.value = SearchPageCountUiState.init()
    }

    fun plusPageCount() {
        val currentCounts = _pageCounts.value

        val imageCount = currentCounts.imagePageCount % MAX_PAGE_COUNT_IMAGE + 1
        val videoCount = currentCounts.videoPageCount % MAX_PAGE_COUNT_VIDEO + 1

        _pageCounts.value = SearchPageCountUiState(imagePageCount = imageCount, videoPageCount = videoCount)
        searchCombinedResults(searchResult.value.keyword.orEmpty())
    }
}