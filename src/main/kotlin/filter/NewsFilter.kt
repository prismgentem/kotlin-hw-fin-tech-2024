package filter

import dto.News
import java.time.LocalDate

interface NewsFilter {
    fun filterAndSortNews(news: List<News>, count: Int, period: ClosedRange<LocalDate>): List<News>
}