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
 * Contains a summary of the place. A summary is comprised of a textual overview, and also includes the language code for these if applicable. Summary text must be presented as-is and can not be modified or altered.
 *
 * @param overview A medium-length textual summary of the place.
 * @param language The language of the previous fields. May not always be present.
 */

data class PlaceEditorialSummary (

    /* A medium-length textual summary of the place. */
    @field:JsonProperty("overview")
    val overview: kotlin.String? = null,

    /* The language of the previous fields. May not always be present. */
    @field:JsonProperty("language")
    val language: kotlin.String? = null

)
