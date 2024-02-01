package com.android.imagesearch.ui.main.util

import androidx.recyclerview.widget.DiffUtil
import com.android.imagesearch.data.SearchModel

object SearchDiffUtil : DiffUtil.ItemCallback<SearchModel>() {

    override fun areItemsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
        return oldItem == newItem
    }
}