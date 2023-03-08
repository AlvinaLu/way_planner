package cz.cvut.fel.lushnalv.models.google

data class Location(
    val lat:Double,
    val lng:Double
) {
    override fun toString(): String {
        return "$lat,$lng"
    }
}
