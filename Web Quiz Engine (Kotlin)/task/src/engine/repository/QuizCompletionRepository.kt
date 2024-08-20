package engine.repository

import engine.entity.QuizCompletion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizCompletionRepository : JpaRepository<QuizCompletion, Long> {
    fun findByUserEmail(userEmail: String, pageable: Pageable): Page<QuizCompletion>
}