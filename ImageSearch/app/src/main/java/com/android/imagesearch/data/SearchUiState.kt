package com.android.imagesearch.data


data class SearchUiState(
    val list: List<SearchModel>,
) {
    companion object {
        fun init() = SearchUiState(
            list = emptyList()
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
