package com.android.imagesearch.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class SearchModel(
    val id: String = UUID.randomUUID().toString(),
    val thumbnailUrl: String?,
    val siteName: String?,
    val datetime: Date?,
    val itemType: SearchListType,
    val isSaved: Boolean = false
) : Parcelable