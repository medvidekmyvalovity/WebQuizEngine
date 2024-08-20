package engine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebQuizEngine

fun main(args: Array<String>) {
    runApplication<WebQuizEngine>(*args)
}



data class QuizResponse(
    val id: Long,
    val title: String,
    val text: String,
    val options: List<String>
)



