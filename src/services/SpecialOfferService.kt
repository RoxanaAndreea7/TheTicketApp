package services

import data.SpecialOffer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Handles creating, searching and removing special offers.
 *
 * Offers are stored in memory only and are identified by an auto-incrementing id.
 */
class SpecialOfferService {

    // All special offers currently in the system.
    private val specialOffers = mutableListOf<SpecialOffer>()
    private var nextOfferId = 1

    // Date format used for user input.
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        // Seed a couple of offers so the menu has something to show.
        seedSampleOffers()
    }

    /**
     * Adds two example offers so that the search / list options can be tested quickly.
     */
    private fun seedSampleOffers() {
        specialOffers.add(
            SpecialOffer(
                id = nextOfferId++,
                stationName = "London",
                discount = 20.0,
                startDate = LocalDate.now().minusDays(5),
                endDate = LocalDate.now().plusDays(10),
                description = "Weekend Special - 20% off"
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
     * Interactive flow for adding a new special offer.
     * Only intended to be used from the admin menu.
     */
    fun addSpecialOffer() {
        println("\n=== ADD SPECIAL OFFER ===")

        print("Enter station name: ")
        val stationName = readLine()?.trim().orEmpty()
        if (stationName.isEmpty()) {
            println("❌ Station name cannot be empty.")
            return
        }

        print("Enter discount percentage (0–100): ")
        val discount = readLine()?.toDoubleOrNull() ?: 0.0
        if (discount <= 0 || discount > 100) {
            println("❌ Discount must be between 0 and 100.")
            return
        }

        // Start date
        print("Enter start date (yyyy-MM-dd): ")
        val startDateStr = readLine().orEmpty()
        val startDate = try {
            LocalDate.parse(startDateStr, dateFormatter)
        } catch (_: DateTimeParseException) {
            println("❌ Invalid start date format.")
            return
        }

        // End date
        print("Enter end date (yyyy-MM-dd): ")
        val endDateStr = readLine().orEmpty()
        val endDate = try {
            LocalDate.parse(endDateStr, dateFormatter)
        } catch (_: DateTimeParseException) {
            println("❌ Invalid end date format.")
            return
        }

        if (endDate.isBefore(startDate)) {
            println("❌ End date cannot be before start date.")
            return
        }

        print("Enter offer description: ")
        val description = readLine()?.trim().takeUnless { it.isNullOrEmpty() } ?: "Special offer"

        // Soft warning if another offer overlaps on the same station.
        val hasOverlap = specialOffers.any { existing ->
            existing.stationName.equals(stationName, ignoreCase = true) &&
                    datesOverlap(existing.startDate, existing.endDate, startDate, endDate)
        }

        if (hasOverlap) {
            println("⚠️ There is already an overlapping offer for this station.")
            print("Continue anyway? (y/n): ")
            val answer = readLine()?.trim()?.lowercase()
            if (answer != "y") {
                println("Operation cancelled.")
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
        println("\n✅ Special offer added.")
        displayOffer(newOffer)
    }

    /**
     * Simple menu for searching offers by different criteria.
     */
    fun searchOffers() {
        println("\n=== SEARCH SPECIAL OFFERS ===")

        if (specialOffers.isEmpty()) {
            println("No special offers available.")
            return
        }

        println(
            """
            Search by:
            1. Station name
            2. Active offers (today)
            3. Date range
            4. View all offers
        """.trimIndent()
        )

        print("Select option (1–4): ")
        when (readLine()?.toIntOrNull()) {
            1 -> {
                print("Enter station name: ")
                val station = readLine().orEmpty()
                val results = specialOffers.filter {
                    it.stationName.contains(station, ignoreCase = true)
                }
                displaySearchResults(results, "Station: $station")
            }

            2 -> {
                val today = LocalDate.now()
                val results = specialOffers.filter { offer ->
                    !today.isBefore(offer.startDate) && !today.isAfter(offer.endDate)
                }
                displaySearchResults(results, "Active offers")
            }

            3 -> {
                print("Enter start date (yyyy-MM-dd): ")
                val start = try {
                    LocalDate.parse(readLine(), dateFormatter)
                } catch (_: Exception) {
                    println("Invalid start date.")
                    return
                }

                print("Enter end date (yyyy-MM-dd): ")
                val end = try {
                    LocalDate.parse(readLine(), dateFormatter)
                } catch (_: Exception) {
                    println("Invalid end date.")
                    return
                }

                val results = specialOffers.filter { offer ->
                    datesOverlap(offer.startDate, offer.endDate, start, end)
                }
                displaySearchResults(results, "Between $start and $end")
            }

            4 -> displayAllOffers()
            else -> println("Invalid option.")
        }
    }

    /**
     * Allows an admin user to remove an offer by id.
     */
    fun deleteOffer() {
        println("\n=== DELETE SPECIAL OFFER ===")

        if (specialOffers.isEmpty()) {
            println("No offers to delete.")
            return
        }

        displayAllOffers()

        print("\nEnter offer id to delete (0 to cancel): ")
        val offerId = readLine()?.toIntOrNull() ?: 0
        if (offerId == 0) {
            println("Operation cancelled.")
            return
        }

        val offer = specialOffers.find { it.id == offerId }
        if (offer == null) {
            println("❌ Offer with id $offerId not found.")
            return
        }

        println("\nOffer selected:")
        displayOffer(offer)

        print("\nConfirm deletion? (y/n): ")
        val confirm = readLine()?.trim()?.lowercase()
        if (confirm == "y") {
            specialOffers.remove(offer)
            println("✅ Offer deleted.")
        } else {
            println("Deletion cancelled.")
        }
    }

    /**
     * Returns all offers that are active today for a given station.
     * This will be used later by Member A when calculating prices.
     */
    fun getActiveOffersForStation(stationName: String): List<SpecialOffer> {
        val today = LocalDate.now()
        return specialOffers.filter { offer ->
            offer.stationName.equals(stationName, ignoreCase = true) &&
                    !today.isBefore(offer.startDate) &&
                    !today.isAfter(offer.endDate)
        }
    }

    private fun displayOffer(offer: SpecialOffer) {
        println("\n" + "-".repeat(40))
        println("ID: ${offer.id}")
        println("Station: ${offer.stationName}")
        println("Discount: ${offer.discount}%")
        println("Valid: ${offer.startDate} to ${offer.endDate}")
        println("Description: ${offer.description}")

        val today = LocalDate.now()
        val status = when {
            offer.endDate.isBefore(today) -> "EXPIRED"
            offer.startDate.isAfter(today) -> "UPCOMING"
            else -> "ACTIVE"
        }
        println("Status: $status")
        println("-".repeat(40))
    }

    private fun displayAllOffers() {
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

    private fun displaySearchResults(results: List<SpecialOffer>, criteria: String) {
        println("\n=== SEARCH RESULTS ===")
        println("Criteria: $criteria")

        if (results.isEmpty()) {
            println("No offers found.")
        } else {
            println("Found ${results.size} offer(s):")
            results.forEach { displayOffer(it) }
        }
    }

    /**
     * Simple overlap check: returns true if the two ranges touch at all.
     */
    private fun datesOverlap(
        start1: LocalDate,
        end1: LocalDate,
        start2: LocalDate,
        end2: LocalDate
    ): Boolean {
        return !(end1.isBefore(start2) || end2.isBefore(start1))
    }

    /**
     * Exposed for tests or debugging – returns a copy of the current list.
     */
    fun getAllOffers(): List<SpecialOffer> = specialOffers.toList()
}
