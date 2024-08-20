package engine.service

import engine.entity.User
import engine.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun registerUser(email: String, password: String) {
        if (userRepository.findByEmail(email) != null) {
            throw IllegalArgumentException("Email is already taken")
        }
        val encodedPassword = passwordEncoder.encode(password)
        val user = User(email = email, password = encodedPassword)
        userRepository.save(user)
    }

}