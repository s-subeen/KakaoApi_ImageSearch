package com.android.imagesearch.ui.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.imagesearch.FormatManager
import com.android.imagesearch.FormatManager.loadImage
import com.android.imagesearch.ui.main.util.SearchDiffUtil
import com.android.imagesearch.data.SearchListType
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.databinding.ImageSearchItemBinding


class SearchListAdapter(
    private val itemClickListener: (SearchModel) -> Unit
) : ListAdapter<SearchModel, SearchListAdapter.ViewHolder>(SearchDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ImageSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ImageSearchItemBinding,
        private val itemClickListener: ((SearchModel) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchModel) = with(binding) {
            item.thumbnailUrl?.let { ivImage.loadImage(it) }
            ivHeart.isVisible = item.isSaved
            tvImageSiteName.text = item.siteName
            tvItemType.text = SearchListType.from(item.itemType)
            tvImageDatetime.text = item.datetime?.let { FormatManager.formatDateToString(it) }
            ivImage.setOnClickListener {
                itemClickListener?.invoke(item)
            }
        }
    }


}