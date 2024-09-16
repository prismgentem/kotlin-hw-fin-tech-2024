package prettyprinter

import dto.News

interface PrettyPrinter {
    fun print(news: Collection<News>)
}