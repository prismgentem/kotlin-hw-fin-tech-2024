package provider

import dto.KudaGoNewsResponse
import dto.News
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class KudaGoNewsProvider(private val client: HttpClient) : NewsProvider {
    override suspend fun getNews(count: Int): List<News> {
        val url = "https://kudago.com/public-api/v1.4/news/"
        val response: HttpResponse = client.get(url) {
            parameter("page_size", count)
            parameter("order_by", "publication_date")
            parameter("location", "spb")
            parameter("fields", "id,title,place,description,site_url,favorites_count,comments_count")
        }


        val jsonResponse = response.bodyAsText()

        val kudaGoNewsResponse = Json.decodeFromString<KudaGoNewsResponse>(jsonResponse)

        return kudaGoNewsResponse.results
    }
}