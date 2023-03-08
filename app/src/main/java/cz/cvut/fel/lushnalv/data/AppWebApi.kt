package cz.cvut.fel.lushnalv.data

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.cvut.fel.lushnalv.data.dto.response.*
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit


private const val BASE_URL = "https://bakalarska-back-end.herokuapp.com/"
private const val BASE_URL2 = "http://192.168.0.185:8080/"
private const val BASE_URL3 = "http://192.168.0.206:8080/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper().registerModule(JavaTimeModule())))
    .client(OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS).build())
    .build()

interface AppWebApi {

    @POST("user/signup")
    fun signUp(@Body user: UserRegistrationRequest): Single<UserDto>

    @POST("user/signin")
    fun signIn(@Body user: UserLoginDto): Single<JwtResponse>

    @POST("user/sign-google")
    fun authorizationWithGoogle(@Body user: UserAuthenticationGoogleRequest): Single<JwtResponse>

    @POST("user/reset-password")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Single<Unit>

    @POST("user/update-password")
    fun updatePassword(@Body updatePasswordRequest: UpdatePasswordRequest): Single<UserDto>

    @GET("user/data/{userId}")
    fun getUserInformation(@Path("userId") userId: Long, @Header("Authorization") authorization: String): Single<UserDto>

    @GET("user/friends")
    @Headers("Content-Type: application/json")
    fun getUserFriends(@Header("Authorization") authorization: String): Single<List<UserDto>>

    @GET("trip/all")
    fun getAllTrips(@Header("Authorization") authorization: String): Single<List<TripDto>>

    @GET("trip/{tripId}")
    fun getTrip(@Path("tripId") tripId : Long, @Header("Authorization") authorization: String): Single<TripDto>

    @GET("trip/{tripId}/info")
    fun getTripInfo(@Path("tripId") tripId : Long, @Header("Authorization") authorization: String): Single<TripInfoDto>

    @POST("trip/new")
    @Headers("Content-Type: application/json")
    fun createNewTrip(@Header("Authorization") authorization: String, @Body newTripDto: NewTripDto): Single<TripDto>

    @POST("day_point/new")
    @Headers("Content-Type: application/json")
    fun createNewDayPoint(@Body newDayPoint: NewDayPointDto, @Header("Authorization") authorization: String): Single<TripDto>

    @POST("day_point/reorder")
    fun reorderDayPoints(@Body reorderDayPointsDto: ReorderDayPointsDto, @Header("Authorization") authorization: String): Single<TripDto>

    @GET("day_point/{dayPointId}")
    fun getDayPoint(@Path("dayPointId") dayPointId : Long, @Header("Authorization") authorization: String): Single<DayPointWholeInfoDto>

    @POST("day_point/add_photo")
    fun dayPointAddPhotos(@Body newPhotoList : NewPhotoListDto, @Header("Authorization") authorization: String): Single<DayPointWholeInfoDto>

    @POST("day_point/change")
    fun changeDuration(@Body changeDuration: ChangeDuration, @Header("Authorization") authorization: String): Single<TripDto>

    @POST("day_point/change_time")
    fun changeStartTime(@Body changeDayPointTimeDto: ChangeDayPointTimeDto, @Header("Authorization") authorization: String): Single<TripDto>

    @POST("day_point/add_document")
    fun dayPointAddDocument(@Body newDocument: NewDocumentDto, @Header("Authorization") authorization: String): Single<DayPointWholeInfoDto>

    @POST("duty/new")
    @Headers("Content-Type: application/json")
    fun createNewDuty(@Body newDutyDto: NewDutyDto, @Header("Authorization") authorization: String): Single<DayPointWholeInfoDto>

    @POST("comment/new")
    @Headers("Content-Type: application/json")
    fun createNewComment(@Body newCommentDto: NewCommentDto, @Header("Authorization") authorization: String): Single<CommentDto>

    @POST("trip/default_photo")
    @Headers("Content-Type: application/json")
    fun changeDefaultPhoto(@Body changePhotoDto: ChangePhotoDto, @Header("Authorization") authorization: String): Single<TripDto>


    @HTTP(method = "DELETE", path = "trip/{tripId}", hasBody = true)
    @Headers("Content-Type: application/json")
    fun deleteTrip(@Path("tripId") tripId : Long, @Header("Authorization") authorization: String): Single<Long>

    @HTTP(method = "DELETE", path = "day_point/{dayPointId}", hasBody = true)
    @Headers("Content-Type: application/json")
    fun deleteDayPoint(@Path("dayPointId") dayPointId : Long, @Header("Authorization") authorization: String): Single<Long>


    @HTTP(method = "DELETE", path = "duty/{dutyId}", hasBody = true)
    @Headers("Content-Type: application/json")
    fun deleteDuty(@Path("dutyId") dutyId : Long, @Header("Authorization") authorization: String): Single<Long>

    @HTTP(method = "DELETE", path = "comment/{commentId}", hasBody = true)
    @Headers("Content-Type: application/json")
    fun deleteComment(@Path("commentId") commentId : Long, @Header("Authorization") authorization: String): Single<Long>


    @HTTP(method = "DELETE", path = "day_point/image", hasBody = true)
    @Headers("Content-Type: application/json")
    fun deleteDayPointImage(@Body deleteImageDto: DeleteImageDto, @Header("Authorization") authorization: String): Single<DeleteImageDto>

    @HTTP(method = "DELETE", path = "day_point/document", hasBody = true)
    @Headers("Content-Type: application/json")
    fun deleteDayPointDocument(@Body deleteDocumentDto: DeleteDocumentDto, @Header("Authorization") authorization: String): Single<DeleteDocumentDto>

    @POST("trip/user/invite")
    fun inviteUser(@Body inviteUserDto: InviteUserDto, @Header("Authorization") authorization: String): Single<UserDto>




    companion object {
        @Volatile
        private var INSTANCE: AppWebApi? = null

        fun getApiService(): AppWebApi {
            return INSTANCE ?: synchronized(this) {
                val instance = retrofit.create(AppWebApi::class.java)
                INSTANCE = instance
                instance
            }
        }
    }
}

data class DeleteImageDto(val dayPointId: Long, val url: String)
data class DeleteDocumentDto(val dayPointId: Long, val url: String)

data class InviteUserDto(
    val tripId: Long,
    val userEmail: String = ""
)
