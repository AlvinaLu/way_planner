package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import cz.cvut.fel.lushnalv.BuildConfig
import cz.cvut.fel.lushnalv.data.googleFindPlacesApi.GoogleFindPlacesApi
import cz.cvut.fel.lushnalv.models.CandidatePlace
import cz.cvut.fel.lushnalv.models.TypeOfDayPoint
import cz.cvut.fel.lushnalv.models.google.Status
import cz.cvut.fel.lushnalv.ui.theme.mapScreen.RequestGooglePlace
import cz.cvut.fel.lushnalv.utils.getStringForRequest
import kotlinx.coroutines.*
import org.openapi.google.model.PlacesDetailsStatus
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CandidatePlaceRepository(
    private val candidatePlaceDao: CandidatePlaceDao,
    private val googleFindPlacesApi: GoogleFindPlacesApi
) {

    val allPlaces: LiveData<List<CandidatePlace>> =
        candidatePlaceDao.getAllCandidatePlaces()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private fun insertCandidatePlace(vararg place: CandidatePlace) {
        coroutineScope.launch(Dispatchers.IO) {
            candidatePlaceDao.insertCandidatePlace(*place)
        }
    }


    fun deleteAllCandidates() {
        coroutineScope.launch(Dispatchers.IO) {
            candidatePlaceDao.deleteAll()
        }
    }

    fun delete(placeId: String) {
        coroutineScope.launch(Dispatchers.IO) {
            candidatePlaceDao.delete(placeId)
        }
    }


    fun findCandidatePlaceByIdLiveData(placeId: String): LiveData<CandidatePlace> {
        return candidatePlaceDao.findCandidatePlaceLiveData(placeId)
    }


    private suspend fun asyncFind(id: String): CandidatePlace? =
        coroutineScope.async(Dispatchers.IO) {
            return@async candidatePlaceDao.findCandidatePlace(id)
        }.await()

    suspend fun getPlacesFromWebBySearch(
        requestGooglePlace: String,
        radius: Int,
        center: LatLng
    ): Status {
        val response =
            googleFindPlacesApi.getCandidatePlacesByKeyword(
                center.getStringForRequest(),
                (radius * 0.53).toInt(),
                requestGooglePlace
            )
        if (response.status == Status.OK) {
            val places = response.results.map {
                val type = choseTypeOfPoint(it.types?.get(0) ?: "")
                CandidatePlace(
                    googleId = it.placeId,
                    name = it.name,
                    address = it.vicinity.toString(),
                    lat = it.geometry.location.lat,
                    lng = it.geometry.location.lng,
                    openNow = it.openingHours?.openNow ?: false,
                    type = type
                )
            }
            val preciseData = places.filter {
                it.name.lowercase().contains(requestGooglePlace.lowercase().trim())
            }
            if (preciseData.isEmpty()) {
                insertCandidatePlace(*places.toTypedArray())
            } else {
                insertCandidatePlace(*preciseData.toTypedArray())
            }

        }
        return response.status
    }


    suspend fun getPlacesFromWeb(
        requestGooglePlace: RequestGooglePlace,
        radius: Int,
        center: LatLng
    ): Status {
        val response = if (requestGooglePlace == RequestGooglePlace.hotel) {
            googleFindPlacesApi.getCandidatePlacesByKeyword(
                center.getStringForRequest(),
                (radius * 0.53).toInt(),
                requestGooglePlace.name
            )
        } else {
            googleFindPlacesApi.getCandidatePlacesByType(
                center.getStringForRequest(),
                (radius * 0.64).toInt(),
                requestGooglePlace.name
            )
        }
        if (response.status == Status.OK) {
            val places = response.results.map {
                var type: TypeOfDayPoint = when (requestGooglePlace) {
                    RequestGooglePlace.restaurant -> TypeOfDayPoint.FOOD
                    RequestGooglePlace.hotel -> TypeOfDayPoint.HOTEL
                    RequestGooglePlace.tourist_attraction -> TypeOfDayPoint.SIGHTS
                    RequestGooglePlace.gas_station -> TypeOfDayPoint.GAS
                    else -> TypeOfDayPoint.SIGHTS
                }
                CandidatePlace(
                    googleId = it.placeId,
                    name = it.name,
                    address = it.vicinity,
                    lat = it.geometry.location.lat,
                    lng = it.geometry.location.lng,
                    openNow = it.openingHours?.openNow ?: false,
                    type = type
                )
            }
            insertCandidatePlace(*places.toTypedArray())
        }
        return response.status
    }



    suspend fun getPlaceDetails(
        placeId: String,
        type: TypeOfDayPoint
    ): PlacesDetailsStatus {
        val mapper = jacksonObjectMapper()
        val response = googleFindPlacesApi.getCandidatePlaceDetails(
            placeId
        )
        if (response.status == PlacesDetailsStatus.OK) {
            var list = mutableListOf<String>()
            response.result.photos?.forEach {
                list.add("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photo_reference=" + it.photoReference + "&key=" + BuildConfig.MAPS_API_KEY)
            }
            if (response.result.placeId != null && response.result.name != null && response.result.geometry?.location?.lat?.toDouble() != null && response.result.geometry?.location?.lng?.toDouble() != null) {
                val placeNew =
                    CandidatePlace(
                        googleId = response.result.placeId,
                        name = response.result.name,
                        addInformation = response.result.editorialSummary?.overview,
                        address = response.result.vicinity,
                        website = response.result.website,
                        lat = response.result.geometry.location.lat.toDouble(),
                        lng = response.result.geometry.location.lng.toDouble(),
                        openNow = response.result.openingHours?.openNow ?: null,
                        openingHours = mapper.writeValueAsString(response.result.openingHours?: ""),
                        rating = response.result.rating?.toDouble(),
                        userRatingsTotal = response.result.userRatingsTotal?.toInt(),
                        type = type,
                    )
                placeNew.photoList = list
                insertCandidatePlace(placeNew)
            }

        }
        return response.status
    }

}

fun choseTypeOfPoint(type: String?): TypeOfDayPoint {
    return when (type) {
        "airport" -> TypeOfDayPoint.CUSTOM
        "amusement_park" -> TypeOfDayPoint.SIGHTS
        "aquarium" -> TypeOfDayPoint.SIGHTS
        "art_gallery" -> TypeOfDayPoint.SIGHTS
        "atm" -> TypeOfDayPoint.CUSTOM
        "bakery" -> TypeOfDayPoint.FOOD
        "bank" -> TypeOfDayPoint.CUSTOM
        "bar" -> TypeOfDayPoint.FOOD
        "bus_station" -> TypeOfDayPoint.CUSTOM
        "cafe" -> TypeOfDayPoint.FOOD
        "campground" -> TypeOfDayPoint.HOTEL
        "car_repair" -> TypeOfDayPoint.CUSTOM
        "car_wash" -> TypeOfDayPoint.CUSTOM
        "casino" -> TypeOfDayPoint.SIGHTS
        "church" -> TypeOfDayPoint.SIGHTS
        "drugstore" -> TypeOfDayPoint.CUSTOM
        "gas_station" -> TypeOfDayPoint.GAS
        "hospital" -> TypeOfDayPoint.CUSTOM
        "light_rail_station" -> TypeOfDayPoint.CUSTOM
        "lodging" -> TypeOfDayPoint.HOTEL
        "meal_delivery" -> TypeOfDayPoint.FOOD
        "movie_theater" -> TypeOfDayPoint.SIGHTS
        "museum" -> TypeOfDayPoint.SIGHTS
        "night_club" -> TypeOfDayPoint.SIGHTS
        "park" -> TypeOfDayPoint.SIGHTS
        "parking" -> TypeOfDayPoint.CUSTOM
        "pharmacy" -> TypeOfDayPoint.CUSTOM
        "post_office" -> TypeOfDayPoint.CUSTOM
        "restaurant" -> TypeOfDayPoint.FOOD
        "subway_station" -> TypeOfDayPoint.CUSTOM
        "supermarket" -> TypeOfDayPoint.FOOD
        "tourist_attraction" -> TypeOfDayPoint.SIGHTS
        "train_station" -> TypeOfDayPoint.CUSTOM
        "transit_station" -> TypeOfDayPoint.CUSTOM
        "zoo" -> TypeOfDayPoint.SIGHTS
        else -> TypeOfDayPoint.CUSTOM
    }
}


fun measure(cameraPositionState: CameraPositionState): Int {
    val lat1: Double? = cameraPositionState.projection?.visibleRegion?.nearLeft?.latitude
    val lng1: Double? = cameraPositionState.projection?.visibleRegion?.nearLeft?.longitude
    val lat2: Double = cameraPositionState.position.target.latitude
    val lng2: Double = cameraPositionState.position.target.longitude
    return if (lat1 != null && lng1 != null) {
        val r = 6378.137; // Radius of earth in KM
        val dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        val dLon = lng2 * Math.PI / 180 - lng1 * Math.PI / 180;
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1 * Math.PI / 180) * cos(lat2 * Math.PI / 180) *
                sin(dLon / 2) * sin(dLon / 2);
        val c = 2 * atan2(sqrt(a), sqrt(1 - a));
        val d = r * c;
        (d * 1000).toInt();
    } else {
        1500
    }
}
