package cz.cvut.fel.lushnalv.models.google

data class Response(
    val status: Status,
    val results: List<Place>
)


enum class Status {
    OK, ZERO_RESULTS, NOT_FOUND, INVALID_REQUEST, OVER_QUERY_LIMIT, REQUEST_DENIED, UNKNOWN_ERROR}
