package engine.entity

import jakarta.persistence.*

@Entity
data class Quiz(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val title: String,
    val text: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quiz_options")
    @Column(name = "option")
    val options: List<String>,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quiz_answers")
    @Column(name = "answer")
    val answer: List<Int>,

    val author: String
)