package provider

import dto.KudaGoNewsResponse
import dto.News
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KudaGoNewsProvider(private val client: HttpClient) : NewsProvider {
    companion object {
        private const val BASE_URL = "https://kudago.com/public-api/v1.4/news/"
        private val logger: Logger = LoggerFactory.getLogger(KudaGoNewsProvider::class.java)
    }

    override suspend fun getNews(count: Int): List<News> {
        return try {
            logger.info("Запрос новостей с URL: $BASE_URL с параметрами count=$count")
            val response: HttpResponse = client.get(BASE_URL) {
                parameter("page_size", count)
                parameter("order_by", "publication_date")
                parameter("location", "spb")
                parameter("fields", "id,title,place,description,site_url,favorites_count,comments_count")
            }

            if (response.status == HttpStatusCode.OK) {
                logger.info("Успешный ответ от сервера: ${response.status}")
                val jsonResponse = response.bodyAsText()
                val kudaGoNewsResponse = Json.decodeFromString<KudaGoNewsResponse>(jsonResponse)
                logger.info("Успешно распарсили новости: ${kudaGoNewsResponse.results.size} записей")
                return kudaGoNewsResponse.results
            } else {
                logger.warn("Получен неуспешный статус: ${response.status}")
                emptyList()
            }
        } catch (e: SerializationException) {
            logger.error("Ошибка парсинга ответа: ${e.message}", e)
            emptyList()
        } catch (e: Exception) {
            logger.error("Произошла ошибка при запросе новостей: ${e.message}", e)
            emptyList()
        }
    }
}