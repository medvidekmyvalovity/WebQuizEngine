package engine.repository

import engine.entity.Quiz
import org.springframework.data.jpa.repository.JpaRepository

interface QuizRepository : JpaRepository<Quiz, Long>

data class SolveRequest(
    val answer: List<Int>
)
