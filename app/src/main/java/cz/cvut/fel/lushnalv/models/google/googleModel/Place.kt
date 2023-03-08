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

import org.openapi.google.model.AddressComponent
import org.openapi.google.model.Geometry
import org.openapi.google.model.PlaceEditorialSummary
import org.openapi.google.model.PlaceOpeningHours
import org.openapi.google.model.PlacePhoto
import org.openapi.google.model.PlaceReview
import org.openapi.google.model.PlusCode

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Attributes describing a place. Not all attributes will be available for all place types.
 *
 * @param addressComponents An array containing the separate components applicable to this address.
 * @param adrAddress A representation of the place's address in the [adr microformat](http://microformats.org/wiki/adr).
 * @param businessStatus Indicates the operational status of the place, if it is a business. If no data exists, `business_status` is not returned. 
 * @param curbsidePickup Specifies if the business supports curbside pickup.
 * @param currentOpeningHours 
 * @param delivery Specifies if the business supports delivery.
 * @param dineIn Specifies if the business supports indoor or outdoor seating options.
 * @param editorialSummary 
 * @param formattedAddress A string containing the human-readable address of this place.  Often this address is equivalent to the postal address. Note that some countries, such as the United Kingdom, do not allow distribution of true postal addresses due to licensing restrictions.  The formatted address is logically composed of one or more address components. For example, the address \"111 8th Avenue, New York, NY\" consists of the following components: \"111\" (the street number), \"8th Avenue\" (the route), \"New York\" (the city) and \"NY\" (the US state).  Do not parse the formatted address programmatically. Instead you should use the individual address components, which the API response includes in addition to the formatted address field. 
 * @param formattedPhoneNumber Contains the place's phone number in its [local format](http://en.wikipedia.org/wiki/Local_conventions_for_writing_telephone_numbers).
 * @param geometry 
 * @param icon Contains the URL of a suggested icon which may be displayed to the user when indicating this result on a map.
 * @param iconBackgroundColor Contains the default HEX color code for the place's category.
 * @param iconMaskBaseUri Contains the URL of a recommended icon, minus the `.svg` or `.png` file type extension.
 * @param internationalPhoneNumber Contains the place's phone number in international format. International format includes the country code, and is prefixed with the plus, +, sign. For example, the international_phone_number for Google's Sydney, Australia office is `+61 2 9374 4000`.
 * @param name Contains the human-readable name for the returned result. For `establishment` results, this is usually the canonicalized business name.
 * @param openingHours 
 * @param permanentlyClosed Use `business_status` to get the operational status of businesses.
 * @param photos An array of photo objects, each containing a reference to an image. A request may return up to ten photos. More information about place photos and how you can use the images in your application can be found in the [Place Photos](https://developers.google.com/maps/documentation/places/web-service/photos) documentation.
 * @param placeId A textual identifier that uniquely identifies a place. To retrieve information about the place, pass this identifier in the `place_id` field of a Places API request. For more information about place IDs, see the [place ID overview](https://developers.google.com/maps/documentation/places/web-service/place-id).
 * @param plusCode 
 * @param priceLevel The price level of the place, on a scale of 0 to 4. The exact amount indicated by a specific value will vary from region to region. Price levels are interpreted as follows: - 0 Free - 1 Inexpensive - 2 Moderate - 3 Expensive - 4 Very Expensive 
 * @param rating Contains the place's rating, from 1.0 to 5.0, based on aggregated user reviews.
 * @param reference 
 * @param reviews A JSON array of up to five reviews. By default, the reviews are sorted in order of relevance. Use the `reviews_sort` request parameter to control sorting.  - For `most_relevant` (default), reviews are sorted by relevance; the service will bias the results to return reviews originally written in the preferred language. - For `newest`, reviews are sorted in chronological order; the preferred language does not affect the sort order. Google recommends indicating to users whether results are ordered by `most_relevant` or `newest`. 
 * @param scope 
 * @param secondaryOpeningHours 
 * @param takeout Specifies if the business supports takeout.
 * @param types Contains an array of feature types describing the given result. See the list of [supported types](https://developers.google.com/maps/documentation/places/web-service/supported_types#table2).
 * @param url Contains the URL of the official Google page for this place. This will be the Google-owned page that contains the best available information about the place. Applications must link to or embed this page on any screen that shows detailed results about the place to the user.
 * @param userRatingsTotal The total number of reviews, with or without text, for this place.
 * @param utcOffset Contains the number of minutes this place’s current timezone is offset from UTC. For example, for places in Sydney, Australia during daylight saving time this would be 660 (+11 hours from UTC), and for places in California outside of daylight saving time this would be -480 (-8 hours from UTC).
 * @param vicinity For establishment (`types:[\"establishment\", ...])` results only, the `vicinity` field contains a simplified address for the place, including the street name, street number, and locality, but not the province/state, postal code, or country.  For all other results, the `vicinity` field contains the name of the narrowest political (`types:[\"political\", ...]`) feature that is present in the address of the result.  This content is meant to be read as-is. Do not programmatically parse the formatted address. 
 * @param website The authoritative website for this place, such as a business' homepage.
 */

data class Place (

    /* An array containing the separate components applicable to this address. */
    @field:JsonProperty("address_components")
    val addressComponents: kotlin.collections.List<AddressComponent>? = null,

    /* A representation of the place's address in the [adr microformat](http://microformats.org/wiki/adr). */
    @field:JsonProperty("adr_address")
    val adrAddress: kotlin.String? = null,

    /* Indicates the operational status of the place, if it is a business. If no data exists, `business_status` is not returned.  */
    @field:JsonProperty("business_status")
    val businessStatus: Place.BusinessStatus? = null,

    /* Specifies if the business supports curbside pickup. */
    @field:JsonProperty("curbside_pickup")
    val curbsidePickup: kotlin.Boolean? = null,

    @field:JsonProperty("current_opening_hours")
    val currentOpeningHours: PlaceOpeningHours? = null,

    /* Specifies if the business supports delivery. */
    @field:JsonProperty("delivery")
    val delivery: kotlin.Boolean? = null,

    /* Specifies if the business supports indoor or outdoor seating options. */
    @field:JsonProperty("dine_in")
    val dineIn: kotlin.Boolean? = null,

    @field:JsonProperty("editorial_summary")
    val editorialSummary: PlaceEditorialSummary? = null,

    /* A string containing the human-readable address of this place.  Often this address is equivalent to the postal address. Note that some countries, such as the United Kingdom, do not allow distribution of true postal addresses due to licensing restrictions.  The formatted address is logically composed of one or more address components. For example, the address \"111 8th Avenue, New York, NY\" consists of the following components: \"111\" (the street number), \"8th Avenue\" (the route), \"New York\" (the city) and \"NY\" (the US state).  Do not parse the formatted address programmatically. Instead you should use the individual address components, which the API response includes in addition to the formatted address field.  */
    @field:JsonProperty("formatted_address")
    val formattedAddress: kotlin.String? = null,

    /* Contains the place's phone number in its [local format](http://en.wikipedia.org/wiki/Local_conventions_for_writing_telephone_numbers). */
    @field:JsonProperty("formatted_phone_number")
    val formattedPhoneNumber: kotlin.String? = null,

    @field:JsonProperty("geometry")
    val geometry: Geometry? = null,

    /* Contains the URL of a suggested icon which may be displayed to the user when indicating this result on a map. */
    @field:JsonProperty("icon")
    val icon: kotlin.String? = null,

    /* Contains the default HEX color code for the place's category. */
    @field:JsonProperty("icon_background_color")
    val iconBackgroundColor: kotlin.String? = null,

    /* Contains the URL of a recommended icon, minus the `.svg` or `.png` file type extension. */
    @field:JsonProperty("icon_mask_base_uri")
    val iconMaskBaseUri: kotlin.String? = null,

    /* Contains the place's phone number in international format. International format includes the country code, and is prefixed with the plus, +, sign. For example, the international_phone_number for Google's Sydney, Australia office is `+61 2 9374 4000`. */
    @field:JsonProperty("international_phone_number")
    val internationalPhoneNumber: kotlin.String? = null,

    /* Contains the human-readable name for the returned result. For `establishment` results, this is usually the canonicalized business name. */
    @field:JsonProperty("name")
    val name: kotlin.String? = null,

    @field:JsonProperty("opening_hours")
    val openingHours: PlaceOpeningHours? = null,

    /* Use `business_status` to get the operational status of businesses. */
    @field:JsonProperty("permanently_closed")
    @Deprecated(message = "This property is deprecated.")
    val permanentlyClosed: kotlin.Boolean? = null,

    /* An array of photo objects, each containing a reference to an image. A request may return up to ten photos. More information about place photos and how you can use the images in your application can be found in the [Place Photos](https://developers.google.com/maps/documentation/places/web-service/photos) documentation. */
    @field:JsonProperty("photos")
    val photos: kotlin.collections.List<PlacePhoto>? = null,

    /* A textual identifier that uniquely identifies a place. To retrieve information about the place, pass this identifier in the `place_id` field of a Places API request. For more information about place IDs, see the [place ID overview](https://developers.google.com/maps/documentation/places/web-service/place-id). */
    @field:JsonProperty("place_id")
    val placeId: kotlin.String? = null,

    @field:JsonProperty("plus_code")
    val plusCode: PlusCode? = null,

    /* The price level of the place, on a scale of 0 to 4. The exact amount indicated by a specific value will vary from region to region. Price levels are interpreted as follows: - 0 Free - 1 Inexpensive - 2 Moderate - 3 Expensive - 4 Very Expensive  */
    @field:JsonProperty("price_level")
    val priceLevel: java.math.BigDecimal? = null,

    /* Contains the place's rating, from 1.0 to 5.0, based on aggregated user reviews. */
    @field:JsonProperty("rating")
    val rating: java.math.BigDecimal? = null,

    @field:JsonProperty("reference")
    @Deprecated(message = "This property is deprecated.")
    val reference: kotlin.String? = null,

    /* A JSON array of up to five reviews. By default, the reviews are sorted in order of relevance. Use the `reviews_sort` request parameter to control sorting.  - For `most_relevant` (default), reviews are sorted by relevance; the service will bias the results to return reviews originally written in the preferred language. - For `newest`, reviews are sorted in chronological order; the preferred language does not affect the sort order. Google recommends indicating to users whether results are ordered by `most_relevant` or `newest`.  */
    @field:JsonProperty("reviews")
    val reviews: kotlin.collections.List<PlaceReview>? = null,

    @field:JsonProperty("scope")
    @Deprecated(message = "This property is deprecated.")
    val scope: kotlin.String? = null,

    @field:JsonProperty("secondary_opening_hours")
    val secondaryOpeningHours: PlaceOpeningHours? = null,

    /* Specifies if the business supports takeout. */
    @field:JsonProperty("takeout")
    val takeout: kotlin.Boolean? = null,

    /* Contains an array of feature types describing the given result. See the list of [supported types](https://developers.google.com/maps/documentation/places/web-service/supported_types#table2). */
    @field:JsonProperty("types")
    val types: kotlin.collections.List<kotlin.String>? = null,

    /* Contains the URL of the official Google page for this place. This will be the Google-owned page that contains the best available information about the place. Applications must link to or embed this page on any screen that shows detailed results about the place to the user. */
    @field:JsonProperty("url")
    val url: kotlin.String? = null,

    /* The total number of reviews, with or without text, for this place. */
    @field:JsonProperty("user_ratings_total")
    val userRatingsTotal: java.math.BigDecimal? = null,

    /* Contains the number of minutes this place’s current timezone is offset from UTC. For example, for places in Sydney, Australia during daylight saving time this would be 660 (+11 hours from UTC), and for places in California outside of daylight saving time this would be -480 (-8 hours from UTC). */
    @field:JsonProperty("utc_offset")
    val utcOffset: java.math.BigDecimal? = null,

    /* For establishment (`types:[\"establishment\", ...])` results only, the `vicinity` field contains a simplified address for the place, including the street name, street number, and locality, but not the province/state, postal code, or country.  For all other results, the `vicinity` field contains the name of the narrowest political (`types:[\"political\", ...]`) feature that is present in the address of the result.  This content is meant to be read as-is. Do not programmatically parse the formatted address.  */
    @field:JsonProperty("vicinity")
    val vicinity: kotlin.String? = null,

    /* The authoritative website for this place, such as a business' homepage. */
    @field:JsonProperty("website")
    val website: kotlin.String? = null

) {

    /**
     * Indicates the operational status of the place, if it is a business. If no data exists, `business_status` is not returned. 
     *
     * Values: OPERATIONAL,CLOSED_TEMPORARILY,CLOSED_PERMANENTLY
     */
    enum class BusinessStatus(val value: kotlin.String) {
        @JsonProperty(value = "OPERATIONAL") OPERATIONAL("OPERATIONAL"),
        @JsonProperty(value = "CLOSED_TEMPORARILY") CLOSED_TEMPORARILY("CLOSED_TEMPORARILY"),
        @JsonProperty(value = "CLOSED_PERMANENTLY") CLOSED_PERMANENTLY("CLOSED_PERMANENTLY");
    }
}

