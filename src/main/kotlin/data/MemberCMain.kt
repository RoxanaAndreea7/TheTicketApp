package data

import data.services.AuthenticationService
import data.services.SpecialOfferService

/**
 * Simple console menu to test Member C features:
 * - login / logout
 * - admin check
 * - manage special offers
 */
fun main() {
    println("=== Ticket System – Member C test ===")

    val authService = AuthenticationService()
    val offerService = SpecialOfferService()

    // allow up to 3 login attempts
    var loggedIn = false
    for (attempt in 1..3) {
        if (authService.login()) {
            loggedIn = true
            break
        }
        println("Login attempt $attempt of 3")
    }

    if (!loggedIn) {
        println("Login failed. Exiting.")
        return
    }

    // main menu loop
    while (true) {
        val user = authService.getCurrentUser()
        println()
        println("--- MAIN MENU ---")
        println("Logged in as: ${user?.username} ${if (user?.isAdmin == true) "[ADMIN]" else "[USER]"}")

        if (authService.isAdmin()) {
            println("1. Add special offer")
            println("2. Search special offers")
            println("3. Delete special offer")
            println("4. View all offers")
        } else {
            println("(Read-only user – admin options disabled)")
        }

        println("8. Log out and log in as someone else")
        println("0. Exit")
        print("Choice: ")

        when (readLine()?.toIntOrNull()) {
            1 -> if (authService.isAdmin()) offerService.addSpecialOffer() else println("Admin access required.")
            2 -> if (authService.isAdmin()) offerService.searchOffers() else println("Admin access required.")
            3 -> if (authService.isAdmin()) offerService.deleteOffer() else println("Admin access required.")
            4 -> if (authService.isAdmin()) offerService.getAllOffers().forEach { println(it) } else println("Admin access required.")
            8 -> {
                authService.logout()
                println()
                println("Log in as a different user:")
                if (!authService.login()) {
                    println("Login failed. Exiting.")
                    break
                }
            }
            0 -> {
                authService.logout()
                println("Application closed.")
                break
            }
            else -> println("Please enter a valid option.")
        }
    }
}
