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

import org.openapi.google.model.PlaceOpeningHoursPeriod
import org.openapi.google.model.PlaceSpecialDay

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An object describing the opening hours of a place.
 *
 * @param openNow A boolean value indicating if the place is open at the current time.
 * @param periods An array of opening periods covering seven days, starting from Sunday, in chronological order. 
 * @param specialDays An array of up to seven entries corresponding to the next seven days. 
 * @param type A type string used to identify the type of secondary hours (for example, `DRIVE_THROUGH`, `HAPPY_HOUR`, `DELIVERY`, `TAKEOUT`, `KITCHEN`, `BREAKFAST`, `LUNCH`, `DINNER`, `BRUNCH`, `PICKUP`, `SENIOR_HOURS`). Set for `secondary_opening_hours` only.
 * @param weekdayText An array of strings describing in human-readable text the hours of the place.
 */

data class PlaceOpeningHours (

    /* A boolean value indicating if the place is open at the current time. */
    @field:JsonProperty("open_now")
    val openNow: kotlin.Boolean? = null,

    /* An array of opening periods covering seven days, starting from Sunday, in chronological order.  */
    @field:JsonProperty("periods")
    val periods: kotlin.collections.List<PlaceOpeningHoursPeriod>? = null,

    /* An array of up to seven entries corresponding to the next seven days.  */
    @field:JsonProperty("special_days")
    val specialDays: kotlin.collections.List<PlaceSpecialDay>? = null,

    /* A type string used to identify the type of secondary hours (for example, `DRIVE_THROUGH`, `HAPPY_HOUR`, `DELIVERY`, `TAKEOUT`, `KITCHEN`, `BREAKFAST`, `LUNCH`, `DINNER`, `BRUNCH`, `PICKUP`, `SENIOR_HOURS`). Set for `secondary_opening_hours` only. */
    @field:JsonProperty("type")
    val type: kotlin.String? = null,

    /* An array of strings describing in human-readable text the hours of the place. */
    @field:JsonProperty("weekday_text")
    val weekdayText: kotlin.collections.List<kotlin.String>? = null

)

