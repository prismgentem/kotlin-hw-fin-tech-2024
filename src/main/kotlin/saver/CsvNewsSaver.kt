package saver

import dto.News
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class CsvNewsSaver : NewsSaver {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(NewsSaver::class.java)
    }

    override fun saveNews(path: String, news: Collection<News>) {
        val filePath = Paths.get(path)

        if (Files.exists(filePath)) {
            logger.error("Файл уже существует по указанному пути: $path")
            throw IllegalArgumentException("Файл уже существует по указанному пути.")
        }

        try {
            logger.info("Создание файла по пути: $path")
            File(path).bufferedWriter().use { writer ->
                logger.info("Запись заголовка в файл")
                writer.write("id,title,place,description,siteUrl,favoritesCount,commentsCount,rating\n")

                news.forEach { n ->
                    logger.debug("Запись новости с id=${n.id} в файл")
                    writer.write("${n.id},${n.title},${n.place},${n.description},${n.siteUrl},${n.favoritesCount},${n.commentsCount},${n.rating}\n")
                }

                logger.info("Успешная запись ${news.size} новостей в файл: $path")
            }
        } catch (e: Exception) {
            logger.error("Ошибка при записи новостей в файл: ${e.message}", e)
            throw e
        }
    }
}