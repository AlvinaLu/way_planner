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


import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An object describing a specific location with Latitude and Longitude in decimal degrees.
 *
 * @param latitude Latitude in decimal degrees
 * @param longitude Longitude in decimal degrees
 */

data class LatitudeLongitudeLiteral (

    /* Latitude in decimal degrees */
    @field:JsonProperty("latitude")
    val latitude: java.math.BigDecimal,

    /* Longitude in decimal degrees */
    @field:JsonProperty("longitude")
    val longitude: java.math.BigDecimal

)

