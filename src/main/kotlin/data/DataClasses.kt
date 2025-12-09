package data

import java.time.LocalDate


enum class TicketType {
    SINGLE, RETURN
}


data class Ticket(
    val origin: String,
    val destination: String,
    val price: Double,
    val ticketType: TicketType,
    val purchaseDate: LocalDate = LocalDate.now()
)


data class Destination(
    var stationName: String,        // var because Member B can update
    var singlePrice: Double,        // var because Member B can update
    var returnPrice: Double,         // var because Member B can update
    var salesCount: Int = 0         // var because Member A updates when selling
)


data class User(
    val username: String,
    val password: String,
    val isAdmin: Boolean
)


data class SpecialOffer(
    val id: Int,
    val stationName: String,
    val discount: Double,           // Percentage (e.g., 20.0 for 20%)
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String
)