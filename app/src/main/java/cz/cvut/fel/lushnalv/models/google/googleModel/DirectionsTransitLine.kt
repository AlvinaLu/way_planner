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

import org.openapi.google.model.DirectionsTransitAgency
import org.openapi.google.model.DirectionsTransitVehicle

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 *
 * @param agencies The transit agency (or agencies) that operates this transit line.
 * @param name The full name of this transit line, e.g. \"8 Avenue Local\".
 * @param color The color commonly used in signage for this line.
 * @param shortName The short name of this transit line. This will normally be a line number, such as \"M7\" or \"355\".
 * @param textColor The color commonly used in signage for this line.
 * @param url Contains the URL for this transit line as provided by the transit agency.
 * @param icon Contains the URL for the icon associated with this line.
 * @param vehicle 
 */

data class DirectionsTransitLine (

    /* The transit agency (or agencies) that operates this transit line. */
    @field:JsonProperty("agencies")
    val agencies: kotlin.collections.List<DirectionsTransitAgency>,

    /* The full name of this transit line, e.g. \"8 Avenue Local\". */
    @field:JsonProperty("name")
    val name: kotlin.String,

    /* The color commonly used in signage for this line. */
    @field:JsonProperty("color")
    val color: kotlin.String? = null,

    /* The short name of this transit line. This will normally be a line number, such as \"M7\" or \"355\". */
    @field:JsonProperty("short_name")
    val shortName: kotlin.String? = null,

    /* The color commonly used in signage for this line. */
    @field:JsonProperty("text_color")
    val textColor: kotlin.String? = null,

    /* Contains the URL for this transit line as provided by the transit agency. */
    @field:JsonProperty("url")
    val url: kotlin.String? = null,

    /* Contains the URL for the icon associated with this line. */
    @field:JsonProperty("icon")
    val icon: kotlin.String? = null,

    @field:JsonProperty("vehicle")
    val vehicle: DirectionsTransitVehicle? = null

)
