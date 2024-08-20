package engine.security

import engine.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsImpl(private val repository: UserRepository) : UserDetailsService {


    override fun loadUserByUsername(username: String?): UserDetails? {
        val user = repository.findByEmail(username!!)
            ?: throw UsernameNotFoundException("Not found")

        return UserAdapter(user)

    }
}