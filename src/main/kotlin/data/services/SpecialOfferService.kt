package main.kotlin.data.services

import data.SpecialOffer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Handles creation, search and removal of special offers.
 *
 * The offers are stored in memory only (no database).
 */
class SpecialOfferService {

    // In-memory list of offers.
    private val specialOffers = mutableListOf<SpecialOffer>()
    private var nextOfferId = 1

    // Shared formatter for all date input/output.
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        // A couple of example offers so the menu has data to work with.
        addSampleOffers()
    }

    private fun addSampleOffers() {
        specialOffers.add(
            SpecialOffer(
                id = nextOfferId++,
                stationName = "London",
                discount = 20.0,
                startDate = LocalDate.now().minusDays(5),
                endDate = LocalDate.now().plusDays(10),
                description = "Weekend special – 20% off"
            )
        )

        specialOffers.add(
            SpecialOffer(
                id = nextOfferId++,
                stationName = "Manchester",
                discount = 15.0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(30),
                description = "Monthly promotion"
            )
        )
    }

    /**
     * Adds a new offer using data entered from the console.
     */
    fun addSpecialOffer() {
        println("\n=== ADD SPECIAL OFFER ===")

        print("Station name: ")
        val stationName = readLine()?.trim().orEmpty()
        if (stationName.isEmpty()) {
            println("Station name cannot be empty.")
            return
        }

        print("Discount percentage (0–100): ")
        val discount = readLine()?.toDoubleOrNull()
        if (discount == null || discount <= 0 || discount > 100) {
            println("Invalid discount value.")
            return
        }

        print("Start date (yyyy-MM-dd): ")
        val startDate = parseDateOrNull(readLine())
        if (startDate == null) {
            println("Start date is not valid.")
            return
        }

        print("End date (yyyy-MM-dd): ")
        val endDate = parseDateOrNull(readLine())
        if (endDate == null) {
            println("End date is not valid.")
            return
        }

        if (endDate.isBefore(startDate)) {
            println("End date cannot be before start date.")
            return
        }

        print("Description: ")
        val description = readLine()?.trim().takeUnless { it.isNullOrEmpty() } ?: "Special offer"

        val hasOverlap = specialOffers.any { existing ->
            existing.stationName.equals(stationName, ignoreCase = true) &&
                    datesOverlap(existing.startDate, existing.endDate, startDate, endDate)
        }

        if (hasOverlap) {
            println("Warning: another offer overlaps this period for the same station.")
            print("Continue anyway? (y/n): ")
            if (!readLine().equals("y", ignoreCase = true)) {
                println("Offer not added.")
                return
            }
        }

        val newOffer = SpecialOffer(
            id = nextOfferId++,
            stationName = stationName,
            discount = discount,
            startDate = startDate,
            endDate = endDate,
            description = description
        )

        specialOffers.add(newOffer)

        println("\nOffer added successfully:")
        displayOffer(newOffer)
    }

    /**
     * Console menu to search for existing offers.
     */
    fun searchOffers() {
        println("\n=== SEARCH SPECIAL OFFERS ===")

        if (specialOffers.isEmpty()) {
            println("No offers available.")
            return
        }

        println("1. By station name")
        println("2. Active today")
        println("3. By date range")
        println("4. Show all")
        print("Choice: ")

        when (readLine()?.toIntOrNull()) {
            1 -> {
                print("Station name: ")
                val station = readLine().orEmpty()
                val results = specialOffers.filter {
                    it.stationName.contains(station, ignoreCase = true)
                }
                displaySearchResults(results, "Station = $station")
            }

            2 -> {
                val today = LocalDate.now()
                val results = specialOffers.filter { offer ->
                    !today.isBefore(offer.startDate) && !today.isAfter(offer.endDate)
                }
                displaySearchResults(results, "Active today")
            }

            3 -> {
                print("Start date (yyyy-MM-dd): ")
                val start = parseDateOrNull(readLine())
                print("End date (yyyy-MM-dd): ")
                val end = parseDateOrNull(readLine())

                if (start == null || end == null) {
                    println("Date range is not valid.")
                    return
                }

                val results = specialOffers.filter { offer ->
                    datesOverlap(offer.startDate, offer.endDate, start, end)
                }
                displaySearchResults(results, "Date range $start to $end")
            }

            4 -> showAllOffers()
            else -> println("Please choose a valid option.")
        }
    }

    /**
     * Deletes an offer by ID.
     */
    fun deleteOffer() {
        println("\n=== DELETE SPECIAL OFFER ===")

        if (specialOffers.isEmpty()) {
            println("No offers to delete.")
            return
        }

        showAllOffers()

        print("\nEnter offer ID to delete (0 to cancel): ")
        val id = readLine()?.toIntOrNull() ?: 0
        if (id == 0) {
            println("Cancelled.")
            return
        }

        val offer = specialOffers.find { it.id == id }
        if (offer == null) {
            println("No offer found with ID $id.")
            return
        }

        println("\nOffer selected:")
        displayOffer(offer)
        print("Confirm delete? (y/n): ")

        if (readLine().equals("y", ignoreCase = true)) {
            specialOffers.remove(offer)
            println("Offer deleted.")
        } else {
            println("Deletion cancelled.")
        }
    }

    /**
     * Returns active offers for a specific station.
     * Used by Member A when calculating ticket prices.
     */
    fun getActiveOffersForStation(stationName: String): List<SpecialOffer> {
        val today = LocalDate.now()
        return specialOffers.filter { offer ->
            offer.stationName.equals(stationName, ignoreCase = true) &&
                    !today.isBefore(offer.startDate) &&
                    !today.isAfter(offer.endDate)
        }
    }

    /**
     * Public helper used by your MemberCMain menu to list all offers.
     */
    fun showAllOffers() {
        println("\n=== ALL SPECIAL OFFERS ===")
        if (specialOffers.isEmpty()) {
            println("No offers available.")
            return
        }
        specialOffers
            .sortedBy { it.startDate }
            .forEach { displayOffer(it) }

        println("\nTotal offers: ${specialOffers.size}")
    }

    private fun displayOffer(offer: SpecialOffer) {
        val today = LocalDate.now()
        val status = when {
            offer.endDate.isBefore(today) -> "EXPIRED"
            offer.startDate.isAfter(today) -> "UPCOMING"
            else -> "ACTIVE"
        }

        println("-".repeat(40))
        println("ID        : ${offer.id}")
        println("Station   : ${offer.stationName}")
        println("Discount  : ${offer.discount}%")
        println("Valid     : ${offer.startDate} to ${offer.endDate}")
        println("Status    : $status")
        println("Details   : ${offer.description}")
    }

    private fun displaySearchResults(results: List<SpecialOffer>, criteria: String) {
        println("\nResults for: $criteria")
        if (results.isEmpty()) {
            println("No offers found.")
        } else {
            results.forEach { displayOffer(it) }
        }
    }

    private fun datesOverlap(
        start1: LocalDate,
        end1: LocalDate,
        start2: LocalDate,
        end2: LocalDate
    ): Boolean {
        return !(end1.isBefore(start2) || end2.isBefore(start1))
    }

    private fun parseDateOrNull(raw: String?): LocalDate? =
        try {
            LocalDate.parse(raw, dateFormatter)
        } catch (_: DateTimeParseException) {
            null
        }

    /**
     * Exposed for testing if you need to inspect the list in unit tests.
     */
    fun getAllOffers(): List<SpecialOffer> = specialOffers.toList()
}
