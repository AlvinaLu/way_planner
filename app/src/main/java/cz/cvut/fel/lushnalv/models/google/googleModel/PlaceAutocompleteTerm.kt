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
 * 
 *
 * @param `value` The text of the term.
 * @param offset Defines the start position of this term in the description, measured in Unicode characters
 */

data class PlaceAutocompleteTerm (

    /* The text of the term. */
    @field:JsonProperty("value")
    val `value`: kotlin.String,

    /* Defines the start position of this term in the description, measured in Unicode characters */
    @field:JsonProperty("offset")
    val offset: java.math.BigDecimal

)

