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
 * A review of the place submitted by a user.
 *
 * @param authorName The name of the user who submitted the review. Anonymous reviews are attributed to \"A Google user\".
 * @param rating The user's overall rating for this place. This is a whole number, ranging from 1 to 5.
 * @param relativeTimeDescription The time that the review was submitted in text, relative to the current time.
 * @param time The time that the review was submitted, measured in the number of seconds since since midnight, January 1, 1970 UTC.
 * @param authorUrl The URL to the user's Google Maps Local Guides profile, if available.
 * @param profilePhotoUrl The URL to the user's profile photo, if available.
 * @param language An IETF language code indicating the language of the returned review. This field contains the main language tag only, and not the secondary tag indicating country or region. For example, all the English reviews are tagged as 'en', and not 'en-AU' or 'en-UK' and so on. This field is empty if there is only a rating with no review text. 
 * @param originalLanguage An IETF language code indicating the original language of the review. If the review has been translated, then `original_language` != `language`. This field contains the main language tag only, and not the secondary tag indicating country or region. For example, all the English reviews are tagged as 'en', and not 'en-AU' or 'en-UK' and so on. This field is empty if there is only a rating with no review text. 
 * @param text The user's review. When reviewing a location with Google Places, text reviews are considered optional. Therefore, this field may be empty. Note that this field may include simple HTML markup. For example, the entity reference `&amp;` may represent an ampersand character.
 * @param translated A boolean value indicating if the review was translated from the original language it was written in. If a review has been translated, corresponding to a value of true, Google recommends that you indicate this to your users. For example, you can add the following string, “Translated by Google”, to the review. 
 */

data class PlaceReview (

    /* The name of the user who submitted the review. Anonymous reviews are attributed to \"A Google user\". */
    @field:JsonProperty("author_name")
    val authorName: kotlin.String,

    /* The user's overall rating for this place. This is a whole number, ranging from 1 to 5. */
    @field:JsonProperty("rating")
    val rating: java.math.BigDecimal,

    /* The time that the review was submitted in text, relative to the current time. */
    @field:JsonProperty("relative_time_description")
    val relativeTimeDescription: kotlin.String,

    /* The time that the review was submitted, measured in the number of seconds since since midnight, January 1, 1970 UTC. */
    @field:JsonProperty("time")
    val time: java.math.BigDecimal,

    /* The URL to the user's Google Maps Local Guides profile, if available. */
    @field:JsonProperty("author_url")
    val authorUrl: kotlin.String? = null,

    /* The URL to the user's profile photo, if available. */
    @field:JsonProperty("profile_photo_url")
    val profilePhotoUrl: kotlin.String? = null,

    /* An IETF language code indicating the language of the returned review. This field contains the main language tag only, and not the secondary tag indicating country or region. For example, all the English reviews are tagged as 'en', and not 'en-AU' or 'en-UK' and so on. This field is empty if there is only a rating with no review text.  */
    @field:JsonProperty("language")
    val language: kotlin.String? = null,

    /* An IETF language code indicating the original language of the review. If the review has been translated, then `original_language` != `language`. This field contains the main language tag only, and not the secondary tag indicating country or region. For example, all the English reviews are tagged as 'en', and not 'en-AU' or 'en-UK' and so on. This field is empty if there is only a rating with no review text.  */
    @field:JsonProperty("original_language")
    val originalLanguage: kotlin.String? = null,

    /* The user's review. When reviewing a location with Google Places, text reviews are considered optional. Therefore, this field may be empty. Note that this field may include simple HTML markup. For example, the entity reference `&amp;` may represent an ampersand character. */
    @field:JsonProperty("text")
    val text: kotlin.String? = null,

    /* A boolean value indicating if the review was translated from the original language it was written in. If a review has been translated, corresponding to a value of true, Google recommends that you indicate this to your users. For example, you can add the following string, “Translated by Google”, to the review.  */
    @field:JsonProperty("translated")
    val translated: kotlin.Boolean? = null

)

