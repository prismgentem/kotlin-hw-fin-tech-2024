package saver

import dto.News

interface NewsSaver {
    fun saveNews(path: String, news: Collection<News>)
}