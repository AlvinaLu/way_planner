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

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 *
 * @param location 
 * @param stepIndex The index of the step containing the waypoint.
 * @param stepInterpolation The position of the waypoint along the step's polyline, expressed as a ratio from 0 to 1.
 */

data class DirectionsViaWaypoint (

    @field:JsonProperty("location")
    val location: LatLngLiteral? = null,

    /* The index of the step containing the waypoint. */
    @field:JsonProperty("step_index")
    val stepIndex: kotlin.Int? = null,

    /* The position of the waypoint along the step's polyline, expressed as a ratio from 0 to 1. */
    @field:JsonProperty("step_interpolation")
    val stepInterpolation: java.math.BigDecimal? = null

)
