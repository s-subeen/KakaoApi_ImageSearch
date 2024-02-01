package com.android.imagesearch.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.imagesearch.Constants.Companion.MAX_PAGE_COUNT_IMAGE
import com.android.imagesearch.Constants.Companion.MAX_PAGE_COUNT_VIDEO
import com.android.imagesearch.R
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.data.SearchPageCountUiState
import com.android.imagesearch.data.SearchUiState
import com.android.imagesearch.repository.ImageSearchRepository
import kotlinx.coroutines.launch

class SearchViewModel(
    private val imageSearchRepository: ImageSearchRepository
) : ViewModel() {

    private val _searchResult = MutableLiveData(SearchUiState.init())
    val searchResult: LiveData<SearchUiState> get() = _searchResult

    private var _pageCounts = MutableLiveData(SearchPageCountUiState.init())
    val pageCounts: LiveData<SearchPageCountUiState> get() = _pageCounts

    private val _searchWord = MutableLiveData<String>()
    val searchWord: LiveData<String> get() = _searchWord

    init {
        getStorageSearchWord()
    }

    fun saveStorageSearchWord(query: String) = viewModelScope.launch {
        imageSearchRepository.saveSearchData(query)
        _searchWord.value = query
    }

    private fun getStorageSearchWord() = viewModelScope.launch {
        _searchWord.value = imageSearchRepository.loadSearchData().orEmpty()
    }

    fun searchCombinedResults(query: String) = viewModelScope.launch {
        try {
            val pageCounts = _pageCounts.value ?: SearchPageCountUiState.init()

            val (imageResponse, videoResponse) =
                imageSearchRepository.searchCombinedResults(
                    query = query,
                    imagePage = pageCounts.imagePageCount,
                    videoPage = pageCounts.videoPageCount
                )

            _searchResult.value = SearchUiState(
                list = (imageResponse.list + videoResponse.list).sortedByDescending { it.datetime }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun reloadStorageItems() = viewModelScope.launch {
        val storageItems = imageSearchRepository.getStorageItems()

        _searchResult.value = _searchResult.value?.copy(
            showSnackMessage = false,
            list = _searchResult.value?.list?.map { currentItem ->
                currentItem.copy(isSaved = storageItems.any { it.id == currentItem.id })
            } ?: emptyList()
        )
    }

    private fun saveStorageImage(searchModel: SearchModel) = viewModelScope.launch {
        imageSearchRepository.saveStorageItem(searchModel)

        updateSnackMessage(R.string.snack_image_save)
    }

    private fun removeStorageItem(searchModel: SearchModel) = viewModelScope.launch {
        imageSearchRepository.removeStorageItem(searchModel)

        updateSnackMessage(R.string.snack_image_delete)
    }

    private fun updateSnackMessage(snackMessage: Int) {
        _searchResult.value = _searchResult.value?.copy(
            showSnackMessage = true,
            snackMessage = snackMessage
        )
    }

    fun updateStorageItem(searchModel: SearchModel) {
        val updatedItem = searchModel.copy(isSaved = searchModel.isSaved.not())

        viewModelScope.launch {
            if (updatedItem.isSaved) {
                saveStorageImage(updatedItem)
            } else {
                removeStorageItem(updatedItem)
            }

            _searchResult.value = _searchResult.value?.copy(
                list = _searchResult.value?.list?.map {
                    if (it.id == updatedItem.id) updatedItem else it
                } ?: emptyList()
            )
        }
    }

    fun resetPageCount() {
        _pageCounts.value = SearchPageCountUiState.init()
    }

    fun plusPageCount() {
        val currentCounts = _pageCounts.value ?: SearchPageCountUiState.init()

        val imageCount = if (currentCounts.imagePageCount < MAX_PAGE_COUNT_IMAGE)
            currentCounts.imagePageCount + 1
        else 1

        val videoCount = if (currentCounts.videoPageCount < MAX_PAGE_COUNT_VIDEO)
            currentCounts.videoPageCount + 1
        else 1

        _pageCounts.value = SearchPageCountUiState(
            imagePageCount = imageCount,
            videoPageCount = videoCount
        )
    }

}