package com.android.imagesearch.data


data class SearchUiState(
    val list: List<SearchModel>,
    val keyword: String? = ""
) {
    companion object {
        fun init() = SearchUiState(
            list = emptyList(),
            keyword = null
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
