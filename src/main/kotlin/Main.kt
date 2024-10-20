import dto.News
import filter.MostRatedNewsFilter
import filter.NewsFilter
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import prettyprinter.ConsolePrettyPrinter
import prettyprinter.PrettyPrinter
import provider.KudaGoNewsProvider
import provider.NewsProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.io.File
import kotlin.system.measureTimeMillis

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            val httpClient = HttpClient(CIO)
            val newsProvider: NewsProvider = KudaGoNewsProvider(httpClient)
            val newsFilter: NewsFilter = MostRatedNewsFilter()
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

            val workers = 2
            val newsPerWorker = count / workers

            val processor = GlobalScope.newsProcessor(File(fileName))

            val time = measureTimeMillis {
                startWorkers(newsProvider, processor, workers, newsPerWorker)
            }

            processor.send(ProcessorMessage.Stop)
            processor.close()

            println("Обработка завершена за $time мс")

            val filteredNews = newsFilter.filterAndSortNews(newsProvider.getNews(count), 10, period)
            prettyPrinter.print(filteredNews)
        }

        suspend fun startWorkers(
            provider: NewsProvider,
            processor: SendChannel<ProcessorMessage>,
            workers: Int,
            newsPerWorker: Int
        ) {
            val threadPool = newFixedThreadPoolContext(workers, "workerPool")

            withContext(threadPool) {
                (1..workers).map { workerId ->
                    async {
                        val newsBatch = provider.getNews(newsPerWorker)
                        processor.send(ProcessorMessage.NewsBatch(newsBatch)) // Отправляем данные в процессор
                        println("Worker $workerId отправил ${newsBatch.size} новостей")
                    }
                }.awaitAll()
            }
        }

        sealed class ProcessorMessage {
            data class NewsBatch(val news: List<News>) : ProcessorMessage()
            object Stop : ProcessorMessage()
        }

        fun CoroutineScope.newsProcessor(file: File) = actor<ProcessorMessage> {
            file.bufferedWriter().use { writer ->
                for (message in channel) {
                    when (message) {
                        is ProcessorMessage.NewsBatch -> {
                            message.news.forEach { news ->
                                writer.write("${news.id},${news.title},${news.description}\n")
                            }
                        }
                        ProcessorMessage.Stop -> {
                            println("Processor завершает работу.")
                            break
                        }
                    }
                }
            }
        }
    }
}
