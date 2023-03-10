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
 * An object containing a numeric value and its formatted text representation.
 *
 * @param text String value.
 * @param `value` Numeric value.
 */

data class TextValueObject (

    /* String value. */
    @field:JsonProperty("text")
    val text: kotlin.String,

    /* Numeric value. */
    @field:JsonProperty("value")
    val `value`: java.math.BigDecimal

)

