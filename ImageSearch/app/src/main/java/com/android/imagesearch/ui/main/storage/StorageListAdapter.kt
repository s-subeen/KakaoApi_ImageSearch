package com.android.imagesearch.ui.main.storage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.imagesearch.FormatManager.formatDateToString
import com.android.imagesearch.FormatManager.loadImage
import com.android.imagesearch.ui.main.util.SearchDiffUtil
import com.android.imagesearch.data.SearchListType
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.databinding.ImageSearchItemBinding

class StorageListAdapter(
    private val itemClickListener: (SearchModel) -> Unit
) : ListAdapter<SearchModel, StorageListAdapter.ViewHolder>(SearchDiffUtil) {

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
            tvImageSiteName.text = item.siteName
            tvItemType.text = SearchListType.from(item.itemType)
            tvImageDatetime.text = item.datetime?.let { formatDateToString(it) }
            ivImage.setOnClickListener {
                itemClickListener?.invoke(item)
            }
        }
    }

}