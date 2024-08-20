package engine.dto

data class QuizRequest(
    val title: String,
    val text: String,
    val options: List<String>,
    val answer: List<Int> = emptyList()
)