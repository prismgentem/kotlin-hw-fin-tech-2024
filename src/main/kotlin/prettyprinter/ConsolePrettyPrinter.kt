package prettyprinter

import dto.News

class ConsolePrettyPrinter : PrettyPrinter {
    override fun print(news: Collection<News>) {
        readme {
            header(level = 1, text = "Топ ${news.size} новостей")
            news.forEachIndexed { index, newsItem ->
                header(level = 2, text = "${index + 1}. ${newsItem.title}")
                text("Рейтинг: ${newsItem.rating}")
                text("Описание:")
                newsItem.description?.let { blockquote(it) }
                unorderedList(listOf("Комментарии: ${newsItem.commentsCount}", "Избранное: ${newsItem.favoritesCount}", "Ссылка: ${newsItem.siteUrl}"))
                separator()
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

        fun blockquote(text: String) {
            println("> $text")
        }

        fun unorderedList(items: List<String>) {
            items.forEach { println("- $it") }
        }

        fun separator() {
            println("-----------")
        }
    }

    fun readme(block: DSLPrettyPrinter.() -> Unit) {
        val printer = DSLPrettyPrinter()
        printer.block()
    }
}
