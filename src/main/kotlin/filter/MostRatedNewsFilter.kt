package filter

import dto.News
import java.time.LocalDate

class MostRatedNewsFilter : NewsFilter {
    override fun filterAndSortNews(news: List<News>, count: Int, period: ClosedRange<LocalDate>): List<News> {
        return news.filter { newsItem ->
            val publicationDate = LocalDate.now()
            publicationDate in period
        }.sortedByDescending { newsItem ->
            newsItem.rating = 1 / (1 + kotlin.math.exp(-(newsItem.favoritesCount / (newsItem.commentsCount + 1.0))))
            newsItem.rating
        }.take(count)
    }
}