package com.android.imagesearch.ui.main.storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.repository.ImageSearchRepository
import kotlinx.coroutines.launch

class StorageViewModel(
    private val imageSearchRepository: ImageSearchRepository
) : ViewModel() {

    private val _storageItems = MutableLiveData<List<SearchModel>>()
    val storageItems: LiveData<List<SearchModel>> get() = _storageItems

    fun removeStorageItem(searchModel: SearchModel) = viewModelScope.launch {
        imageSearchRepository.removeStorageItem(searchModel)
        getStorageItems()
    }

    fun getStorageItems() = viewModelScope.launch {
            val images = imageSearchRepository.getStorageItems()
            _storageItems.value = images
        }
}