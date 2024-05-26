package com.android.imagesearch.ui.storage


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.ui.search.SearchList
import com.android.imagesearch.ui.search.SearchViewModel

data class BottomSheetState(
    val isOpen: Boolean = false,
    val searchModel: SearchModel? = null
)

@Composable
fun StorageScreen(viewModel: StorageViewModel = hiltViewModel()) {
    val searchState by viewModel.storageItems.observeAsState()
    var bottomSheetState by remember { mutableStateOf(BottomSheetState()) }

    LaunchedEffect(viewModel) {
        viewModel.getStorageItems()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SearchList(
            items = searchState ?: emptyList(),
            onItemClick = { searchModel ->
                bottomSheetState = BottomSheetState(isOpen = true, searchModel = searchModel)
            },
            onScrollEnd = { viewModel.getStorageItems() }
        )
    }

    if (bottomSheetState.isOpen) {
        val currentSearchModel = bottomSheetState.searchModel ?: error("searchModel is null")
        BottomSheetDialog(
            onRemoveClickListener = {
                viewModel.removeStorageItem(currentSearchModel)
                bottomSheetState = BottomSheetState()
            },
            searchModel = currentSearchModel,
            dismissBottomSheet = { bottomSheetState = BottomSheetState() }
        )
    }
}