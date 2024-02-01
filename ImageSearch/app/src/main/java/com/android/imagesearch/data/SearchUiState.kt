package com.android.imagesearch.data


data class SearchUiState(
    val list: List<SearchModel>,
    val showSnackMessage: Boolean = false,
    val snackMessage: Int? = null
) {
    companion object {
        fun init() = SearchUiState(
            list = emptyList(),
            showSnackMessage = false,
            snackMessage = null
        )
    }
}


data class SearchPageCountUiState(
    val imagePageCount: Int,
    val videoPageCount: Int,
) {
    companion object {
        fun init() = SearchPageCountUiState(
            imagePageCount = 1,
            videoPageCount = 1,
        )
    }
}
