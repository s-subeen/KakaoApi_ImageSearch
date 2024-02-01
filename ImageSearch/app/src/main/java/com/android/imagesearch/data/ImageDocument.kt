package com.android.imagesearch.data


import com.google.gson.annotations.SerializedName
import java.util.Date

data class ImageDocument(
    @SerializedName("collection")
    val collection: String?, // 컬렉션
    @SerializedName("datetime")
    val dateTime: Date?, // 미리보기 이미지 URL
    @SerializedName("display_sitename")
    val displaySiteName: String?, // 이미지 URL
    @SerializedName("doc_url")
    val docUrl: String?, // 이미지의 가로 길이
    @SerializedName("height")
    val height: Int?, // 이미지의 세로 길이
    @SerializedName("image_url")
    val imageUrl: String?, // 출처
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String?, // 문서 URL
    @SerializedName("width")
    val width: Int?, // 문서 작성시간
)

data class VideoDocument(
    val title: String?, // 동영상 제목
    val url: String?, // 동영상 링크
    @SerializedName("datetime")
    val dateTime: Date?, // 동영상 등록일
    @SerializedName("play_time")
    val playTime: Int?, // 동영상 재생시간, 초 단위
    @SerializedName("thumbnail")
    val thumbnailUrl: String?, // 동영상 미리보기 URL
    val author: String?, // 동영상 업로더
)

data class SearchResponse<T>(
    @SerializedName("meta")
    val metaData: MetaData?,
    @SerializedName("documents")
    var documents: MutableList<T>?
)

data class MetaData(
    @SerializedName("total_count")
    val totalCount: Int?,
    @SerializedName("is_end")
    val isEnd: Boolean?
)
