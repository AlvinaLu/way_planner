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
 * The `status` field within the Streetview Metadata response object contains the status of the request. The `status` field may contain the following values:  - `OK` indicates that no errors occurred; a panorama is found and metadata is returned. - `INVALID_REQUEST` indicates that the request was malformed. - `NOT_FOUND` indicates that the address string provided in the `location` parameter could not be found. This may occur if a non-existent address is given. - `ZERO_RESULTS` indicates that no panorama could be found near the provided location. This may occur if a non-existent or invalid `pano` id is given. - `OVER_QUERY_LIMIT` indicates the requestor has exceeded quota. - `REQUEST_DENIED` indicates that your request was denied. This may occur if you did not [authorize](https://developers.google.com/maps/documentation/streetview/get-api-key) your request, or if the Street View Static API is not activated in the Google Cloud Console project containing your API key. - `UNKNOWN_ERROR` indicates that the request could not be processed due to a server error. This is often a temporary status. The request may succeed if you try again 
 *
 * Values: OK,INVALID_REQUEST,NOT_FOUND,ZERO_RESULTS,OVER_QUERY_LIMIT,REQUEST_DENIED,UNKNOWN_ERROR
 */

enum class StreetViewStatus(val value: kotlin.String) {

    @JsonProperty(value = "OK")
    OK("OK"),

    @JsonProperty(value = "INVALID_REQUEST")
    INVALID_REQUEST("INVALID_REQUEST"),

    @JsonProperty(value = "NOT_FOUND")
    NOT_FOUND("NOT_FOUND"),

    @JsonProperty(value = "ZERO_RESULTS")
    ZERO_RESULTS("ZERO_RESULTS"),

    @JsonProperty(value = "OVER_QUERY_LIMIT")
    OVER_QUERY_LIMIT("OVER_QUERY_LIMIT"),

    @JsonProperty(value = "REQUEST_DENIED")
    REQUEST_DENIED("REQUEST_DENIED"),

    @JsonProperty(value = "UNKNOWN_ERROR")
    UNKNOWN_ERROR("UNKNOWN_ERROR");

    /**
     * Override toString() to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): String = value

    companion object {
        /**
         * Converts the provided [data] to a [String] on success, null otherwise.
         */
        fun encode(data: kotlin.Any?): kotlin.String? = if (data is StreetViewStatus) "$data" else null

        /**
         * Returns a valid [StreetViewStatus] for [data], null otherwise.
         */
        fun decode(data: kotlin.Any?): StreetViewStatus? = data?.let {
          val normalizedData = "$it".lowercase()
          values().firstOrNull { value ->
            it == value || normalizedData == "$value".lowercase()
          }
        }
    }
}

