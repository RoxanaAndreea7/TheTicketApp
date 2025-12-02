package models

data class Destination(
    var name: String,           // Station name
    var singlePrice: Double,     // Price for single ticket
    var returnPrice: Double,     // Price for return ticket
    var ticketsSold: Int = 0,    // Number of tickets sold to this destination
    var takings: Double = 0.0    // Total revenue from this destination
)
}

fun recordSale(price: Double) {
    // add the record of the sales
    ticketsSold++
    takings += price
    println("Sale recorded for $name: Ticket #$ticketsSold, Total takings: £%.2f".format(takings))
}

fun adjustPrices(factor: Double) {

    // Update prices by a given factor
    // Used for bulk price adjustments (increases or decreases)


    if (factor <= 0) { //handles errors in case of a negative number
        println("Error: Factor must be positive")
        return
    }

    val oldSingle = singlePrice
    val oldReturn = returnPrice

    singlePrice = (singlePrice * factor).roundTo2DecimalPlaces()
    returnPrice = (returnPrice * factor).roundTo2DecimalPlaces()

    println("Prices updated for $name:")
    println("  Single: £%.2f → £%.2f".format(oldSingle, singlePrice))
    println("  Return: £%.2f → £%.2f".format(oldReturn, returnPrice))
}