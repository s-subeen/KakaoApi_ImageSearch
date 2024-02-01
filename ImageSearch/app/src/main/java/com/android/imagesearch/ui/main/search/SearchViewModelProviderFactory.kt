package com.android.imagesearch.ui.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.imagesearch.repository.ImageSearchRepository

class SearchViewModelProviderFactory(
    private val imageSearchRepository: ImageSearchRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(
                imageSearchRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel class not found")
    }
}
