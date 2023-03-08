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

import org.openapi.google.model.ErrorDetail

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 *
 * @param code This is the same as the HTTP status of the response.
 * @param message A short description of the error.
 * @param errors A list of errors which occurred. Each error contains an identifier for the type of error and a short description.
 * @param status A status code that indicates the error type.
 * @param details Additional details about the error.
 */

data class ErrorObject (

    /* This is the same as the HTTP status of the response. */
    @field:JsonProperty("code")
    val code: java.math.BigDecimal,

    /* A short description of the error. */
    @field:JsonProperty("message")
    val message: kotlin.String,

    /* A list of errors which occurred. Each error contains an identifier for the type of error and a short description. */
    @field:JsonProperty("errors")
    val errors: kotlin.collections.List<ErrorDetail>,

    /* A status code that indicates the error type. */
    @field:JsonProperty("status")
    val status: kotlin.String? = null,

    /* Additional details about the error. */
    @field:JsonProperty("details")
    val details: kotlin.collections.List<ErrorDetail>? = null

)

