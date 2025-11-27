package models

class destination(
    var name: String = "",
    var singlePrice: Double = 0.0,
    var returnPrice: Double = 0.0,
    var salesCount: Int = 0,
    val activeOffers: MutableList<SpecialOffer> = mutableListOf()
){

}