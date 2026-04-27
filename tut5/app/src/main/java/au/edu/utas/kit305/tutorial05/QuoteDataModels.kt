package au.edu.utas.kit305.tutorial05

data class SpaceQuoteData(
    val space: Space,
    var included: Boolean = false
)

data class RoomQuoteData(
    val room: Room,
    var included: Boolean = false,
    val spaces: MutableList<SpaceQuoteData> = mutableListOf()
)

data class HouseQuoteData(
    val house: House,
    val rooms: MutableList<RoomQuoteData> = mutableListOf()
)