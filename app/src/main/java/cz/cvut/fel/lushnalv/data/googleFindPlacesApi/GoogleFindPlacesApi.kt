package cz.cvut.fel.lushnalv.data.googleFindPlacesApi

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.android.gms.maps.model.LatLng
import cz.cvut.fel.lushnalv.BuildConfig
import cz.cvut.fel.lushnalv.models.google.Response
import org.openapi.google.model.PlacesDetailsResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.CompletableFuture

private const val BASE_URL = "https://maps.googleapis.com/maps/api/place/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)))
    .build()

interface GoogleFindPlacesApi {
    @GET("nearbysearch/json")
    suspend fun getCandidatePlacesByType(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") key: String = BuildConfig.MAPS_API_KEY, ): Response

    @GET("nearbysearch/json")
    suspend fun getCandidatePlacesByKeyword(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("keyword") type: String,
        @Query("key") key: String = BuildConfig.MAPS_API_KEY, ): Response

    @GET("details/json")
    suspend fun getCandidatePlaceDetails(
        @Query("place_id") place_id: String,
        @Query("key") key: String = BuildConfig.MAPS_API_KEY, ): PlacesDetailsResponse




    companion object {
        @Volatile
        private var INSTANCE: GoogleFindPlacesApi? = null

        fun getApiService(): GoogleFindPlacesApi {
            return INSTANCE ?: synchronized(this) {
                val instance = retrofit.create(GoogleFindPlacesApi::class.java)
                INSTANCE = instance
                instance
            }
        }
    }
}