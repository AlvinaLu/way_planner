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
import org.openapi.google.model.DirectionsLeg
import org.openapi.google.model.DirectionsPolyline
import org.openapi.google.model.Fare

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Routes consist of nested `legs` and `steps`.
 *
 * @param legs An array which contains information about a leg of the route, between two locations within the given route. A separate leg will be present for each waypoint or destination specified. (A route with no waypoints will contain exactly one leg within the legs array.) Each leg consists of a series of steps.
 * @param bounds 
 * @param copyrights Contains an array of warnings to be displayed when showing these directions. You must handle and display these warnings yourself.
 * @param summary Contains a short textual description for the route, suitable for naming and disambiguating the route from alternatives.
 * @param waypointOrder An array indicating the order of any waypoints in the calculated route. This waypoints may be reordered if the request was passed optimize:true within its waypoints parameter.
 * @param warnings Contains an array of warnings to be displayed when showing these directions. You must handle and display these warnings yourself.
 * @param overviewPolyline 
 * @param fare 
 */

data class DirectionsRoute (

    /* An array which contains information about a leg of the route, between two locations within the given route. A separate leg will be present for each waypoint or destination specified. (A route with no waypoints will contain exactly one leg within the legs array.) Each leg consists of a series of steps. */
    @field:JsonProperty("legs")
    val legs: kotlin.collections.List<DirectionsLeg>,

    @field:JsonProperty("bounds")
    val bounds: Bounds,

    /* Contains an array of warnings to be displayed when showing these directions. You must handle and display these warnings yourself. */
    @field:JsonProperty("copyrights")
    val copyrights: kotlin.String,

    /* Contains a short textual description for the route, suitable for naming and disambiguating the route from alternatives. */
    @field:JsonProperty("summary")
    val summary: kotlin.String,

    /* An array indicating the order of any waypoints in the calculated route. This waypoints may be reordered if the request was passed optimize:true within its waypoints parameter. */
    @field:JsonProperty("waypoint_order")
    val waypointOrder: kotlin.collections.List<kotlin.Int>,

    /* Contains an array of warnings to be displayed when showing these directions. You must handle and display these warnings yourself. */
    @field:JsonProperty("warnings")
    val warnings: kotlin.collections.List<kotlin.String>,

    @field:JsonProperty("overview_polyline")
    val overviewPolyline: DirectionsPolyline,

    @field:JsonProperty("fare")
    val fare: Fare? = null

)

