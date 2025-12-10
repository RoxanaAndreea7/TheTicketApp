package data.services

import data.User

/**
 * Basic authentication service used by the ticket system.
 *
 * This class stores a fixed list of users (no database),
 * allows login/logout, keeps track of failed attempts,
 * and provides simple admin validation.
 */
class AuthenticationService {

    // Fixed list of users available in the system
    private val users = listOf(
        User("admin", "admin123", true),
        User("manager", "manage456", true),
        User("roxana", "rox789", true),
        User("user1", "pass1", false),
        User("user2", "pass2", false),
        User("guest", "guest", false)
    )

    // Tracks the current logged-in user (if any)
    private var currentUser: User? = null

    // Limits the number of failed login attempts
    private var loginAttempts = 0
    private val maxAttempts = 3

    /**
     * Attempts to log in a user based on console input.
     * Returns true if login is successful.
     */
    fun login(): Boolean {

        // Already logged in
        if (currentUser != null) {
            println("Already logged in as: ${currentUser!!.username}")
            return true
        }

        // Too many failed attempts
        if (loginAttempts >= maxAttempts) {
            println("Maximum login attempts reached.")
            return false
        }

        println("\n--- Login ---")

        print("Username: ")
        val username = readLine()?.trim().orEmpty()

        print("Password: ")
        val password = readLine()?.trim().orEmpty()

        // Check credentials
        val user = users.find { it.username == username && it.password == password }

        return if (user != null) {
            currentUser = user
            loginAttempts = 0
            println("Login successful. Welcome, ${user.username}.")

            if (user.isAdmin) {
                println("Admin access granted.")
            }

            true
        } else {
            loginAttempts++
            val remaining = maxAttempts - loginAttempts
            println("Incorrect username or password. Attempts left: $remaining")
            false
        }
    }

    /**
     * Logs out the current user.
     */
    fun logout() {
        if (currentUser != null) {
            println("Goodbye, ${currentUser!!.username}.")
            currentUser = null
            loginAttempts = 0
        } else {
            println("No user is currently logged in.")
        }
    }

    /**
     * Returns true if the logged-in user is an admin.
     */
    fun isAdmin(): Boolean {
        return currentUser?.isAdmin == true
    }

    /**
     * Returns the currently logged-in user.
     */
    fun getCurrentUser(): User? {
        return currentUser
    }

    /**
     * Used for testing or resetting login state.
     */
    fun resetLoginAttempts() {
        loginAttempts = 0
    }
}
