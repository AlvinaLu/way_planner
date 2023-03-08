package cz.cvut.fel.lushnalv.data.dto.response

import com.google.android.gms.maps.model.LatLng
import cz.cvut.fel.lushnalv.models.CurrencyCode
import cz.cvut.fel.lushnalv.models.TypeOfDayPoint
import cz.cvut.fel.lushnalv.models.TypeOfDayPointActive
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime

data class UserRegistrationRequest(val name: String, val email: String, val password: String)

class UserLoginDto(val email: String, val password: String)

data class UserDto(val id: Long, val email: String, val name: String, val imgUrl: String)

data class ResetPasswordRequest(val email: String)

data class UpdatePasswordRequest(val email: String, val password: String, val code: Int)

class JwtResponse(
    val id: Long,
    val email: String,
    val name: String,
    var imgUrl: String,
    var accessToken: String,
) {
    var type = "Bearer"
}

data class UserAuthenticationGoogleRequest(val idToken: String)

data class TripDto(
    val id: Long,
    val title: String,
    val ownerId: Long,
    val startDay: LocalDateTime,
    val endDay: LocalDateTime,
    val defaultPhoto: String,
    val days: List<DayDto>,
    val members: List<UserDto>,
    val deleted: Boolean,
)

data class TripInfoDto(
    val id: Long,
    val title: String,
    val ownerId: Long,
    val startDay: LocalDateTime,
    val endDay: LocalDateTime,
    val defaultPhoto: String,
    val days: List<DayDto>,
    val members: List<UserDto>,
    val dutyCalculation: List<DutyCalculationDto>,
    val deleted: Boolean,
)

data class DutyCalculationDto(
    val sourceUserId:Long,
    val targetUserId:Long,
    val amount:BigDecimal,
    val currency: CurrencyCode
)




data class DayDto(
    val id: Long,
    val date: LocalDateTime,
    var tripId: Long,
    var dayPoints: List<DayPointDto>,
    val codeWeather: Int,
    val minTemperature: Double,
    val maxTemperature: Double,
)


data class DayPointDto(
    val id: Long,
    val title: String,
    val date: LocalDateTime,
    val duration: Duration,
    val latitude: Double,
    val longitude: Double,
    val typeOfDayPoint: TypeOfDayPoint,
    var dayId: Long,
    val defaultPhoto: String,
    var photoListString: List<String>,
    var documentListString: List<String>,
    val travelTime: Duration,
    val travelType: TypeOfDayPointActive,
    val travelDistance: Int,
    val openingMessage: String,
    val deleted: Boolean,
)

data class DayPointWholeInfoDto(
    val id: Long,
    val title: String,
    val date: LocalDateTime,
    val duration: Duration,
    val latitude: Double,
    val longitude: Double,
    val typeOfDayPoint: TypeOfDayPoint,
    var dayId: Long,
    val defaultPhoto: String,
    var photoListString: List<String>,
    var documentListString: List<String>,
    val duties: List<DutyDto>,
    val travelTime: Duration,
    val travelType: TypeOfDayPointActive,
    val travelDistance: Int,
    val openingMessage: String,
    val deleted: Boolean,
    val users: List<UserDto>,
    val comments: List<CommentDto>,
)


data class DutyDto(
    val dutyId: Long,
    val title: String,
    val author: Long,
    val amount: BigDecimal,
    val currency: CurrencyCode,
    var dayPointId: Long,
    var users: List<Long>,
    val deleted: Boolean,
)

data class NewDutyDto(
    val title: String,
    val amount: BigDecimal,
    val currency: CurrencyCode,
    var dayPointId: Long,
    var users: List<Long>,
)


data class NewTripDto(
    val title: String,
    val startDay: LocalDateTime,
    val endDay: LocalDateTime,
    val startLocation: LatLng,
    val membersIdExist: List<Long>,
    val newMemberEmails: Set<String>,
    val openingHours: String = "",
)

data class NewDayPointDto(
    val title: String,
    val duration: Duration,
    val typeOfDayPoint: TypeOfDayPoint,
    var dayId: Long,
    val lat: Double,
    val lng: Double,
    val defaultPhoto: String = "",
    val openingHours: String = "",
)

data class CommentDto(
    val id: Long,
    val date: LocalDateTime,
    val message: String,
    val author: Long,
    val dayPointId: Long,
    val deleted: Boolean
    )

data class NewPhotoListDto(
    val dayPointId: Long,
    val photoList: MutableList<String> =  mutableListOf(),
)

data class ReorderDayPointsDto(
    val movingId: Long,
    val targetBeforeId: Long
)

data class NewCommentDto(
    val dayPointId: Long,
    val text: String,
)

data class NewDocumentDto(
    val dayPointId: Long,
    val document: String,
)

data class ChangeDuration(
    val dayPointId: Long,
    val duration: Duration,
)

data class ChangePhotoDto(
    val tripId: Long,
    val photoUrl: String
)

data class ChangeDayPointTimeDto(
    val dayPointId: Long,
    val date: LocalDateTime
)

