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

import org.openapi.google.model.LatLngLiteral
import org.openapi.google.model.StreetViewStatus

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 *
 * @param status 
 * @param copyright An array of snapped points.
 * @param date A string indicating year and month that the panorama was captured.
 * @param location 
 * @param panoId A specific panorama ID. These are generally stable, though panoramas may change ID over time as imagery is refreshed.
 */

data class StreetViewResponse (

    @field:JsonProperty("status")
    val status: StreetViewStatus,

    /* An array of snapped points. */
    @field:JsonProperty("copyright")
    val copyright: kotlin.String? = null,

    /* A string indicating year and month that the panorama was captured. */
    @field:JsonProperty("date")
    val date: kotlin.String? = null,

    @field:JsonProperty("location")
    val location: LatLngLiteral? = null,

    /* A specific panorama ID. These are generally stable, though panoramas may change ID over time as imagery is refreshed. */
    @field:JsonProperty("pano_id")
    val panoId: kotlin.String? = null

)

