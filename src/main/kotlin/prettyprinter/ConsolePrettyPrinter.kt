package prettyprinter

import dto.News

class ConsolePrettyPrinter : PrettyPrinter {
    override fun print(news: Collection<News>) {
        readme {
            header(level = 1, text = "Топ ${news.size} новостей")
            news.forEach { newsItem ->
                text("${newsItem.title} (Рейтинг: ${newsItem.rating})")
            }
        }
    }
    class DSLPrettyPrinter {
        fun header(level: Int, text: String) {
            println("${"#".repeat(level)} $text")
        }

        fun text(text: String) {
            println(text)
        }
    }
    fun readme(block: DSLPrettyPrinter.() -> Unit) {
        val printer = DSLPrettyPrinter()
        printer.block()
    }
}