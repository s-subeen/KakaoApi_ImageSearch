package com.android.imagesearch.ui.util

import android.widget.ImageView
import com.android.imagesearch.ui.util.Constants.Companion.DATE_FORMAT
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Constants {
    companion object {
        // retrofit
        const val BASE_URL = "https://dapi.kakao.com"
        const val AUTH_HEADER = "KakaoAK 0f47a50e0449a5d5e3747a997fd12b6c"
        // preference
        const val SEARCH_WORD = "search_keyword"
        const val PREFERENCE_NAME = "pref"
        const val STORAGE_ITEMS = "favorite_items"

        const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        // page count
        const val MAX_PAGE_COUNT_IMAGE = 50
        const val MAX_PAGE_COUNT_VIDEO = 15
        const val MAX_SIZE_IMAGE = 80
        const val MAX_SIZE_VIDEO = 30
        const val SORT_TYPE = "accuracy"

        // navigation
        const val SEARCH = "search"
        const val STORAGE = "storage"
    }
}

object FormatManager {
    fun formatDateToString(date: Date): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(date)
    }

    fun ImageView.loadImage(url: String) {
        this.clipToOutline = true
        Glide.with(this)
            .load(url)
            .into(this)
    }

    fun formatDate(date: Date?): String {
        if (date == null) {
            return ""
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(date)
    }

}