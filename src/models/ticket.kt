package models

import java.time.LocalDateTime

class Ticket (
    var origin:String = "" ,
    var destination:String = "" ,
    var ticketType:String = "" ,
    var price : Double = 0.0 ,
    val purchaseDateTime : LocalDateTime = LocalDateTime.now(),
    val ticketID : String = ""
){
    companion object {
        private var ticketCounter = 1000
        const val ORIGIN_STATION_NAME = "Central Station"
}
   //adding the generate ticket id function

    fun generateTicketId(): String {
        ticketCounter++
        return "TKT-${ticketCounter}-${System.currentTimeMillis() % 10000}"
    }
}

val ticketId: String = generateTicketId()