package com.android.imagesearch.ui.main.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.imagesearch.repository.ImageSearchRepository

class StorageViewModelProviderFactory(
    private val imageSearchRepository: ImageSearchRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StorageViewModel::class.java)) {
            return StorageViewModel(
                imageSearchRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel class not found")
    }
}