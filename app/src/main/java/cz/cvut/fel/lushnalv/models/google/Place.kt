package cz.cvut.fel.lushnalv.models.google

import com.fasterxml.jackson.annotation.JsonProperty

data class Place(
    @JsonProperty("place_id")
    val placeId:String,
    @JsonProperty("business_status")
    val businessStatus: BusinessStatus?,
    val geometry: Geometry,
    val icon:String?,
    @JsonProperty("icon_background_color")
    val iconBackgroundColor: String?,
    @JsonProperty("icon_mask_base_uri")
    val iconMaskBaseUri:String?,
    val name:String,
    @JsonProperty("opening_hours")
    val openingHours:OpeningHours?,
    @JsonProperty("plus_code")
    val plusCode:PlusCode?,
    val rating: Double,
    val types:List<String>?,
    val vicinity: String
)

data class OpeningHours (@JsonProperty("open_now") val openNow:Boolean)

enum class BusinessStatus {
    OPERATIONAL, CLOSED_TEMPORARILY, CLOSED_PERMANENTLY
}
