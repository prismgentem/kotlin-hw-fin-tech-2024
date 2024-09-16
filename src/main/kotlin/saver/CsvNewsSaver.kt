package saver

import dto.News
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class CsvNewsSaver : NewsSaver {
    override fun saveNews(path: String, news: Collection<News>) {
        val filePath = Paths.get(path)

        if (Files.exists(filePath)){
            throw IllegalArgumentException("Файл уже существует по указанному пути.")
        }

        File(path).bufferedWriter().use { writer ->
            writer.write("id,title,place,description,siteUrl,favoritesCount,commentsCount,rating\n")
            news.forEach { n ->
                writer.write("${n.id},${n.title},${n.place},${n.description},${n.siteUrl},${n.favoritesCount},${n.commentsCount},${n.rating}\n")
            }
        }
    }
}