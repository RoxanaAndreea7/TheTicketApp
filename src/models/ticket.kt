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
}
