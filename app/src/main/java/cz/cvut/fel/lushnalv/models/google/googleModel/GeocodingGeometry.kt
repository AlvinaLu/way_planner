/**
 * Google Maps Platform
 *
 * API Specification for Google Maps Platform
 *
 * The version of the OpenAPI document: 1.20.0
 * 
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapi.google.model

import org.openapi.google.model.Bounds
import org.openapi.google.model.LatLngLiteral

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An object describing the location.
 *
 * @param location 
 * @param locationType Stores additional data about the specified location. The following values are currently supported:  - \"ROOFTOP\" indicates that the returned result is a precise geocode for which we have location information accurate down to street address precision. - \"RANGE_INTERPOLATED\" indicates that the returned result reflects an approximation (usually on a road) interpolated between two precise points (such as intersections). Interpolated results are generally returned when rooftop geocodes are unavailable for a street address. - \"GEOMETRIC_CENTER\" indicates that the returned result is the geometric center of a result such as a polyline (for example, a street) or polygon (region). - \"APPROXIMATE\" indicates that the returned result is approximate. 
 * @param viewport 
 * @param bounds 
 */

data class GeocodingGeometry (

    @field:JsonProperty("location")
    val location: LatLngLiteral,

    /* Stores additional data about the specified location. The following values are currently supported:  - \"ROOFTOP\" indicates that the returned result is a precise geocode for which we have location information accurate down to street address precision. - \"RANGE_INTERPOLATED\" indicates that the returned result reflects an approximation (usually on a road) interpolated between two precise points (such as intersections). Interpolated results are generally returned when rooftop geocodes are unavailable for a street address. - \"GEOMETRIC_CENTER\" indicates that the returned result is the geometric center of a result such as a polyline (for example, a street) or polygon (region). - \"APPROXIMATE\" indicates that the returned result is approximate.  */
    @field:JsonProperty("location_type")
    val locationType: GeocodingGeometry.LocationType,

    @field:JsonProperty("viewport")
    val viewport: Bounds,

    @field:JsonProperty("bounds")
    val bounds: Bounds? = null

) {

    /**
     * Stores additional data about the specified location. The following values are currently supported:  - \"ROOFTOP\" indicates that the returned result is a precise geocode for which we have location information accurate down to street address precision. - \"RANGE_INTERPOLATED\" indicates that the returned result reflects an approximation (usually on a road) interpolated between two precise points (such as intersections). Interpolated results are generally returned when rooftop geocodes are unavailable for a street address. - \"GEOMETRIC_CENTER\" indicates that the returned result is the geometric center of a result such as a polyline (for example, a street) or polygon (region). - \"APPROXIMATE\" indicates that the returned result is approximate. 
     *
     * Values: ROOFTOP,RANGE_INTERPOLATED,GEOMETRIC_CENTER,APPROXIMATE
     */
    enum class LocationType(val value: kotlin.String) {
        @JsonProperty(value = "ROOFTOP") ROOFTOP("ROOFTOP"),
        @JsonProperty(value = "RANGE_INTERPOLATED") RANGE_INTERPOLATED("RANGE_INTERPOLATED"),
        @JsonProperty(value = "GEOMETRIC_CENTER") GEOMETRIC_CENTER("GEOMETRIC_CENTER"),
        @JsonProperty(value = "APPROXIMATE") APPROXIMATE("APPROXIMATE");
    }
}

