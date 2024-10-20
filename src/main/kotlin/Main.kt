import filter.MostRatedNewsFilter
import filter.NewsFilter
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking
import prettyprinter.ConsolePrettyPrinter
import prettyprinter.PrettyPrinter
import provider.KudaGoNewsProvider
import provider.NewsProvider
import saver.CsvNewsSaver
import saver.NewsSaver
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Main {
    companion object {
        @JvmStatic

        fun main(args: Array<String>) = runBlocking {
            val httpClient = HttpClient(CIO)
            val newsProvider: NewsProvider = KudaGoNewsProvider(httpClient)
            val newsFilter: NewsFilter = MostRatedNewsFilter()
            val newsSaver: NewsSaver = CsvNewsSaver()
            val prettyPrinter: PrettyPrinter = ConsolePrettyPrinter()

            println("Введите количество новостей или нажмите Enter для использования значения по умолчанию:")
            val count = readLine()?.toIntOrNull() ?: 100

            println("Введите название файла для сохранения новостей с расширением .csv или нажмите Enter для использования значения по умолчанию 'news.csv':")
            val fileNameInput = readLine()
            val fileName = if (fileNameInput.isNullOrBlank()) "news.csv" else fileNameInput

            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            println("Введите дату начала периода (в формате yyyy-MM-dd) или нажмите Enter для использования значения по умолчанию:")
            val startDateInput = readLine()
            val startDate = startDateInput?.takeIf { it.isNotBlank() }?.let {
                LocalDate.parse(it, dateFormatter)
            } ?: LocalDate.now().minusMonths(1)

            println("Введите дату конца периода (в формате yyyy-MM-dd) или нажмите Enter для использования значения по умолчанию:")
            val endDateInput = readLine()
            val endDate = endDateInput?.takeIf { it.isNotBlank() }?.let {
                LocalDate.parse(it, dateFormatter)
            } ?: LocalDate.now()

            val period = startDate..endDate

            val newsList = newsProvider.getNews(count)

            val mostRatedNews = newsFilter.filterAndSortNews(newsList, 10, period)

            newsSaver.saveNews(fileName, mostRatedNews)

            prettyPrinter.print(mostRatedNews)
        }
    }
}
