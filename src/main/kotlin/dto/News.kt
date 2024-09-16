package dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class News(
    val id: Int?,
    val title: String?,
    val place: Place?,
    val description: String?,
    @SerialName("site_url")
    val siteUrl: String?,
    @SerialName("favorites_count")
    val favoritesCount: Int,
    @SerialName("comments_count")
    val commentsCount: Int,
    var rating: Double = 0.0
)
