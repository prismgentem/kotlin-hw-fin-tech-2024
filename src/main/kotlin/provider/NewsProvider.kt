package provider

import dto.News

interface NewsProvider {
    suspend fun getNews(count: Int): List<News>
}