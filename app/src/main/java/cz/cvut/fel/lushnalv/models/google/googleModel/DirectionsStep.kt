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

import org.openapi.google.model.DirectionsPolyline
import org.openapi.google.model.DirectionsTransitDetails
import org.openapi.google.model.LatLngLiteral
import org.openapi.google.model.TextValueObject
import org.openapi.google.model.TravelMode

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Each element in the steps array defines a single step of the calculated directions. A step is the most atomic unit of a direction's route, containing a single step describing a specific, single instruction on the journey. E.g. \"Turn left at W. 4th St.\" The step not only describes the instruction but also contains distance and duration information relating to how this step relates to the following step. For example, a step denoted as \"Merge onto I-80 West\" may contain a duration of \"37 miles\" and \"40 minutes,\" indicating that the next step is 37 miles/40 minutes from this step.  When using the Directions API to search for transit directions, the steps array will include additional transit details in the form of a transit_details array. If the directions include multiple modes of transportation, detailed directions will be provided for walking or driving steps in an inner steps array. For example, a walking step will include directions from the start and end locations: \"Walk to Innes Ave & Fitch St\". That step will include detailed walking directions for that route in the inner steps array, such as: \"Head north-west\", \"Turn left onto Arelious Walker\", and \"Turn left onto Innes Ave\". 
 *
 * @param duration 
 * @param endLocation 
 * @param htmlInstructions Contains formatted instructions for this step, presented as an HTML text string. This content is meant to be read as-is. Do not programmatically parse this display-only content.
 * @param polyline 
 * @param startLocation 
 * @param travelMode 
 * @param distance 
 * @param maneuver Contains the action to take for the current step (turn left, merge, straight, etc.). Values are subject to change, and new values may be introduced without prior notice.
 * @param transitDetails 
 * @param steps Contains detailed directions for walking or driving steps in transit directions. Substeps are only available when travel_mode is set to \"transit\". The inner steps array is of the same type as steps.
 */

data class DirectionsStep (

    @field:JsonProperty("duration")
    val duration: TextValueObject,

    @field:JsonProperty("end_location")
    val endLocation: LatLngLiteral,

    /* Contains formatted instructions for this step, presented as an HTML text string. This content is meant to be read as-is. Do not programmatically parse this display-only content. */
    @field:JsonProperty("html_instructions")
    val htmlInstructions: kotlin.String,

    @field:JsonProperty("polyline")
    val polyline: DirectionsPolyline,

    @field:JsonProperty("start_location")
    val startLocation: LatLngLiteral,

    @field:JsonProperty("travel_mode")
    val travelMode: TravelMode,

    @field:JsonProperty("distance")
    val distance: TextValueObject? = null,

    /* Contains the action to take for the current step (turn left, merge, straight, etc.). Values are subject to change, and new values may be introduced without prior notice. */
    @field:JsonProperty("maneuver")
    val maneuver: DirectionsStep.Maneuver? = null,

    @field:JsonProperty("transit_details")
    val transitDetails: DirectionsTransitDetails? = null,

    /* Contains detailed directions for walking or driving steps in transit directions. Substeps are only available when travel_mode is set to \"transit\". The inner steps array is of the same type as steps. */
    @field:JsonProperty("steps")
    val steps: kotlin.Any? = null

) {

    /**
     * Contains the action to take for the current step (turn left, merge, straight, etc.). Values are subject to change, and new values may be introduced without prior notice.
     *
     * Values: TURN_MINUS_SLIGHT_MINUS_LEFT,TURN_MINUS_SHARP_MINUS_LEFT,TURN_MINUS_LEFT,TURN_MINUS_SLIGHT_MINUS_RIGHT,TURN_MINUS_SHARP_MINUS_RIGHT,KEEP_MINUS_RIGHT,KEEP_MINUS_LEFT,UTURN_MINUS_LEFT,UTURN_MINUS_RIGHT,TURN_MINUS_RIGHT,STRAIGHT,RAMP_MINUS_LEFT,RAMP_MINUS_RIGHT,MERGE,FORK_MINUS_LEFT,FORK_MINUS_RIGHT,FERRY,FERRY_MINUS_TRAIN,ROUNDABOUT_MINUS_LEFT,ROUNDABOUT_MINUS_RIGHT
     */
    enum class Maneuver(val value: kotlin.String) {
        @JsonProperty(value = "turn-slight-left") TURN_MINUS_SLIGHT_MINUS_LEFT("turn-slight-left"),
        @JsonProperty(value = "turn-sharp-left") TURN_MINUS_SHARP_MINUS_LEFT("turn-sharp-left"),
        @JsonProperty(value = "turn-left") TURN_MINUS_LEFT("turn-left"),
        @JsonProperty(value = "turn-slight-right") TURN_MINUS_SLIGHT_MINUS_RIGHT("turn-slight-right"),
        @JsonProperty(value = "turn-sharp-right") TURN_MINUS_SHARP_MINUS_RIGHT("turn-sharp-right"),
        @JsonProperty(value = "keep-right") KEEP_MINUS_RIGHT("keep-right"),
        @JsonProperty(value = "keep-left") KEEP_MINUS_LEFT("keep-left"),
        @JsonProperty(value = "uturn-left") UTURN_MINUS_LEFT("uturn-left"),
        @JsonProperty(value = "uturn-right") UTURN_MINUS_RIGHT("uturn-right"),
        @JsonProperty(value = "turn-right") TURN_MINUS_RIGHT("turn-right"),
        @JsonProperty(value = "straight") STRAIGHT("straight"),
        @JsonProperty(value = "ramp-left") RAMP_MINUS_LEFT("ramp-left"),
        @JsonProperty(value = "ramp-right") RAMP_MINUS_RIGHT("ramp-right"),
        @JsonProperty(value = "merge") MERGE("merge"),
        @JsonProperty(value = "fork-left") FORK_MINUS_LEFT("fork-left"),
        @JsonProperty(value = "fork-right") FORK_MINUS_RIGHT("fork-right"),
        @JsonProperty(value = "ferry") FERRY("ferry"),
        @JsonProperty(value = "ferry-train") FERRY_MINUS_TRAIN("ferry-train"),
        @JsonProperty(value = "roundabout-left") ROUNDABOUT_MINUS_LEFT("roundabout-left"),
        @JsonProperty(value = "roundabout-right") ROUNDABOUT_MINUS_RIGHT("roundabout-right");
    }
}

