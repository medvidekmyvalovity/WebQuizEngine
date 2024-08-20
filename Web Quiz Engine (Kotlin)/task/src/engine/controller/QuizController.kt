package engine.controller

import engine.*
import engine.dto.QuizRequest
import engine.entity.Quiz
import engine.entity.QuizCompletion
import engine.repository.QuizCompletionRepository
import engine.repository.SolveRequest
import engine.service.QuizService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/quizzes")
class QuizController(private val quizService: QuizService, private val quizCompletionRepository: QuizCompletionRepository) {

    @PostMapping
    fun createQuiz(@RequestBody request: QuizRequest): ResponseEntity<Quiz> {
        println("Create Quiz: $request")
        val email = SecurityContextHolder.getContext().authentication.name
        if (request.title.isBlank() || request.text.isBlank() || request.options.size < 2) {
            return ResponseEntity.badRequest().build()
        }
        val quiz = quizService.createQuiz(request, email)
        return ResponseEntity.ok(quiz)
    }

    @GetMapping("/{id}")
    fun getQuiz(@PathVariable id: Long): ResponseEntity<QuizResponse> {
        println("Get Quiz: $id")
        val quiz = quizService.getQuizById(id) ?: return ResponseEntity.notFound().build()
        val response = QuizResponse(
            id = quiz.id ?: 0,
            title = quiz.title,
            text = quiz.text,
            options = quiz.options
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllQuizzes(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Map<String, Any>> {
        val quizPage = quizService.getAllQuizzes(page, size)
        val response: Map<String, Any> = mapOf(
            "content" to quizPage.content.map { quiz ->
                mapOf<String, Any>(
                    "id" to (quiz.id ?: 0),
                    "title" to quiz.title,
                    "text" to quiz.text,
                    "options" to quiz.options
                )
            },
            "totalPages" to quizPage.totalPages,
            "totalElements" to quizPage.totalElements,
            "last" to quizPage.isLast,
            "first" to quizPage.isFirst,
            "sort" to mapOf(
                "empty" to quizPage.sort.isEmpty,
                "sorted" to quizPage.sort.isSorted,
                "unsorted" to quizPage.sort.isUnsorted
            ),
            "number" to quizPage.number,
            "numberOfElements" to quizPage.numberOfElements,
            "size" to quizPage.size,
            "empty" to quizPage.isEmpty,
            "pageable" to mapOf(
                "sort" to mapOf(
                    "empty" to quizPage.pageable.sort.isEmpty,
                    "sorted" to quizPage.pageable.sort.isSorted,
                    "unsorted" to quizPage.pageable.sort.isUnsorted
                ),
                "offset" to quizPage.pageable.offset,
                "pageNumber" to quizPage.pageable.pageNumber,
                "pageSize" to quizPage.pageable.pageSize,
                "paged" to quizPage.pageable.isPaged,
                "unpaged" to quizPage.pageable.isUnpaged
            )

        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/solve")
    fun solveQuiz(@PathVariable id: Long, @RequestBody request: SolveRequest): ResponseEntity<Map<String, Any>> {
        val quiz = quizService.getQuizById(id) ?: return ResponseEntity.notFound().build()
        val isCorrect = quiz.answer.sorted() == request.answer.sorted()
        val response = mapOf(
            "success" to isCorrect,
            "feedback" to if (isCorrect) "Congratulations, you're right!" else "Wrong answer! Please, try again."
        )
        if (isCorrect) {
            val userEmail = SecurityContextHolder.getContext().authentication.name
            val quizCompletion = QuizCompletion(
                quizId = id,
                userEmail = userEmail,
                completedAt = LocalDateTime.now()
            )
            quizCompletionRepository.save(quizCompletion)
        }
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteQuiz(@PathVariable id: Long): ResponseEntity<Unit> {
        val email = SecurityContextHolder.getContext().authentication.name
        val quiz = quizService.getQuizById(id) ?: return ResponseEntity.notFound().build()
        if (quiz.author != email) {
            return ResponseEntity.status(403).build()
        }
        quizService.deleteQuiz(id)
        return ResponseEntity.noContent().build()
    }
    @GetMapping("/completed")
    fun getCompletedQuizzes(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Map<String, Any>> {
        val userEmail = SecurityContextHolder.getContext().authentication.name
        val completionsPage = quizCompletionRepository.findByUserEmail(userEmail, PageRequest.of(page, size))

        // Extract the list of completed quizzes directly
        val response: Map<String, Any> = mapOf(
            "content" to completionsPage.content.map { completion ->
                mapOf(
                    "id" to completion.quizId,
                    "completedAt" to completion.completedAt.toString()
                )
            },
            "totalPages" to completionsPage.totalPages,
            "totalElements" to completionsPage.totalElements,
            "last" to completionsPage.isLast,
            "first" to completionsPage.isFirst,
            "size" to completionsPage.size,
            "number" to completionsPage.number,
            "numberOfElements" to completionsPage.numberOfElements,
            "empty" to completionsPage.isEmpty
        )

        return ResponseEntity.ok(response)
    }
}
