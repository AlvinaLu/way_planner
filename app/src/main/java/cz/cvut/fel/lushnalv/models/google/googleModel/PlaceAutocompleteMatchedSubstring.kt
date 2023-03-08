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
 * @param length Length of the matched substring in the prediction result text.
 * @param offset Start location of the matched substring in the prediction result text.
 */

data class PlaceAutocompleteMatchedSubstring (

    /* Length of the matched substring in the prediction result text. */
    @field:JsonProperty("length")
    val length: java.math.BigDecimal,

    /* Start location of the matched substring in the prediction result text. */
    @field:JsonProperty("offset")
    val offset: java.math.BigDecimal

)

