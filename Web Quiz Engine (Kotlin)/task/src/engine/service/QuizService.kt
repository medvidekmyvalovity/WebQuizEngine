package engine.service


import engine.dto.QuizRequest
import engine.entity.Quiz
import engine.repository.QuizRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class QuizService(private val quizRepository: QuizRepository) {

    @Transactional
    fun createQuiz(request: QuizRequest, email: String): Quiz {
        val quiz = Quiz(
            title = request.title,
            text = request.text,
            options = request.options,
            answer = request.answer,
            author = email
        )
        return quizRepository.save(quiz)
    }

    fun getQuizById(id: Long): Quiz? {
        return quizRepository.findById(id).orElse(null)
    }

    fun getAllQuizzes(page: Int, size: Int): Page<Quiz> {
        val pageable = PageRequest.of(page, size)
        return quizRepository.findAll(pageable)
    }

    @Transactional
    fun deleteQuiz(id: Long) {
        quizRepository.deleteById(id)
    }
}