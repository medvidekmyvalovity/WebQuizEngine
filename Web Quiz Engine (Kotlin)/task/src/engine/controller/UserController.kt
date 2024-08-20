package engine.controller

import engine.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody request: Map<String, String>): ResponseEntity<Unit> {
        val email = request["email"] ?: return ResponseEntity.badRequest().build()
        val password = request["password"] ?: return ResponseEntity.badRequest().build()

        if (!email.contains("@") || !email.contains(".") || password.length < 5) {
            return ResponseEntity.badRequest().build()
        }

        return try {
            userService.registerUser(email, password)
            ResponseEntity.ok().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
}