package models

data class Destination(
    var name: String,           // Station name
    var singlePrice: Double,     // Price for single ticket
    var returnPrice: Double,     // Price for return ticket
    var ticketsSold: Int = 0,    // Number of tickets sold to this destination
    var takings: Double = 0.0    // Total revenue from this destination
)