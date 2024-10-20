package dto

import dto.News
import kotlinx.serialization.Serializable

@Serializable
data class KudaGoNewsResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<News>
)