package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.cvut.fel.lushnalv.data.dto.response.*
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.adapter.rxjava2.Result
import retrofit2.adapter.rxjava2.Result.response


data class TripWithCommentsAndDuties(
    var trip: Trip,
    var listComments: List<Comment>,
)

class TripRepository(
    private val tripDao: TripDao,
    private val dayDao: DayDao,
    private val dayPointDao: DayPointDao,
    private val userDao: UserDao,
    private val tripWithUsersDao: TripWithUsersDao,
    private val tripWithUsersAndDutyCalculationDao: TripWithUsersAndDutyCalculationDao,
    private val dutyCalculationDao: DutyCalculationDao,
    private val dutyDao: DutyDao,
    private val dutyWithUsersDao: DutyWithUsersDao,
    private val commentDao: CommentDao,
    private val appWebApi: AppWebApi
) {


    val allTrips: LiveData<List<Trip>> =
        tripDao.getAllTrips()

    val allTripsWithDaysWithPoints: LiveData<List<TripWithDaysAndWithPoints>> =
        tripDao.getAllTripsWithDaysWithPoints()

    val allTripsWithUsers: LiveData<List<TripWithUsers>> =
        tripDao.getAllTripsWithUsers()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private fun insertTrip(vararg trip: Trip) {
        coroutineScope.launch(Dispatchers.IO) {
            tripDao.insetTrip(*trip)
        }
    }

    fun deleteAll() {
        coroutineScope.launch(Dispatchers.IO) {
            tripDao.deleteAll()
        }
    }

    fun findTripById(tripId: Long): LiveData<TripWithDays> {
        return tripDao.findTripById(tripId)
    }

    fun findTripWithDaysAndDayPointsById(tripId: Long): LiveData<TripWithDaysAndWithPoints> {
        return tripDao.findTripWithDaysAndDayPointsById(tripId)
    }

    fun findTripWithUsersById(tripId: Long): LiveData<TripWithUsers> {
        val trip = tripDao.getTripWithUsers(tripId)
        return trip
    }

    fun findTripWithUsersAndDutyCalculationById(tripId: Long): LiveData<TripWithUsersAndDutyCalculation> {
        val trip = tripDao.getTripWithUsersAndDutyCalculation(tripId)
        return trip
    }

    suspend fun getTripsFromWeb(token: String): Boolean {
        val response = appWebApi.getAllTrips(token).blockingGet()
        response?.forEach { trip ->
            saveData(trip)
        }
        return true
    }

    suspend fun deleteTrip(tripId: Long, token: String): Boolean {
        val response =
            appWebApi.deleteTrip(authorization = token, tripId = tripId).blockingGet()
        deleteData(response)
        return true

    }

    suspend fun createNewTrip(newTrip: NewTripDto, token: String): Boolean {
        val response =
            appWebApi.createNewTrip(authorization = token, newTripDto = newTrip).blockingGet()
        saveData(response)
        return true

    }

    suspend fun getTripFromWeb(token: String, tripId: Long): Boolean {
        val response = appWebApi.getTrip(tripId, token).blockingGet()
        saveData(response)
        return true
    }

    suspend fun getTripInfoFromWeb(token: String, tripId: Long): Boolean {
        val response = appWebApi.getTripInfo(tripId, token).blockingGet()
        saveTripInfoData(response)
        return true
    }

    suspend fun deleteDayPoint(dayPointId: Long, token: String): Boolean {
        val response = appWebApi.deleteDayPoint(dayPointId, token).blockingGet()
        val dayPoint = dayPointDao.findDayPointsAndDutiesAndCommentsById(response)
        if(dayPoint!=null) {
            dayPoint.comments.forEach {
                commentDao.delete(it.commentId)
            }
            dayPoint.duties.forEach {
                dutyWithUsersDao.delete(it.dutyId)
                dutyDao.delete(it.dutyId)
            }
            dayPointDao.delete(dayPoint.dayPoint.dayPointId)
        }
        return true
    }

    suspend fun inviteUser(token: String, inviteUserDto: InviteUserDto): Boolean {
        val response = appWebApi.inviteUser(inviteUserDto, token).blockingGet()
        val user = User(
            userId = response.id,
            email = response.email,
            name = response.name,
            imgUrl = response.imgUrl
        )
        userDao.insertUser(user)
        tripWithUsersDao.insert(UserTripCrossRef(inviteUserDto.tripId, user.userId))
        return true
    }

    suspend fun createNewDayPoint(newDayPoint: NewDayPointDto, token: String): Boolean {
        val response = appWebApi.createNewDayPoint(authorization = token, newDayPoint = newDayPoint)
            .blockingGet()
        saveData(response)
        return true
    }

    suspend fun reorderDayPoints(reorderDayPointsDto: ReorderDayPointsDto, token: String): Boolean {
        val response = appWebApi.reorderDayPoints(reorderDayPointsDto, token).blockingGet()
        saveData(response)
        return true
    }

    suspend fun changeDurationDayPoint(changeDuration: ChangeDuration, token: String): Boolean {
        val response = appWebApi.changeDuration(changeDuration, token).blockingGet()
        saveData(response)
        return true
    }

    suspend fun changeStartTimeDayPoint(changeDayPointTimeDto: ChangeDayPointTimeDto, token: String): Boolean {
        val response = appWebApi.changeStartTime(changeDayPointTimeDto, token).blockingGet()
        saveData(response)
        return true
    }

    suspend fun changeDefaultPhoto(changePhotoDto: ChangePhotoDto, token: String): Boolean {
        val response = appWebApi.changeDefaultPhoto(changePhotoDto, token).blockingGet()
        saveData(response)
        return true
    }

    private suspend fun deleteData(tripId: Long){
        val trip = tripDao.findSimplyTripWithDaysAndDayPointsById(tripId)
        if(trip != null){
            trip.daysWithPoints.forEach { day->
                day.points.forEach { point->
                    val dayPoint = dayPointDao.findDayPointsAndDutiesAndCommentsById(point.dayPointId)
                    if(dayPoint!=null) {
                        dayPoint.comments.forEach {
                            commentDao.delete(it.commentId)
                        }
                        dayPoint.duties.forEach {
                            dutyWithUsersDao.delete(it.dutyId)
                            dutyDao.delete(it.dutyId)
                        }
                        dayPointDao.delete(dayPoint.dayPoint.dayPointId)
                    }
                }
                dayDao.delete(day.day.dayId)

            }
            val tripWIthDutyCalculation = tripWithUsersAndDutyCalculationDao.getSimpleTrip(trip.trip.tripId)
            if(tripWIthDutyCalculation != null) {
                tripWIthDutyCalculation.dutyCalculations.forEach {
                    tripWithUsersAndDutyCalculationDao.delete(tripWIthDutyCalculation.trip.tripId)
                    it.dutyCalculationId?.let { it1 -> dutyCalculationDao.delete(it1) }
                }
                tripWIthDutyCalculation.users.forEach {
                    tripWithUsersDao.delete(tripWIthDutyCalculation.trip.tripId)
                }
            }
            tripDao.delete(trip.trip.tripId)
        }
    }

    private suspend fun saveData(tripDto: TripDto) {
        val dayPoints = mutableListOf<DayPoint>()
        val days = mutableListOf<Day>()
        val users = mutableListOf<User>()

        if(tripDto.deleted){
            deleteData(tripDto.id)
        }else {
            val trip = Trip(
                tripId = tripDto.id,
                title = tripDto.title,
                ownerId = tripDto.ownerId,
                startDay = tripDto.startDay,
                endDay = tripDto.endDay,
                defaultPhoto = tripDto.defaultPhoto
            )
            tripDto.days.map {
                days.add(
                    Day(
                        dayId = it.id,
                        date = it.date,
                        tripId = it.tripId,
                        codeWeather = it.codeWeather,
                        minTemperature = it.minTemperature,
                        maxTemperature = it.maxTemperature

                    )
                )
            }


            tripDto.days.forEach { day ->
                day.dayPoints.onEach { dayPoint ->
                    if (dayPoint.deleted) {
                        val dayPointResult =
                            dayPointDao.findDayPointsAndDutiesAndCommentsById(dayPoint.id)
                        if (dayPointResult != null) {
                            dayPointResult.comments.forEach {
                                commentDao.delete(it.commentId)
                            }
                            dayPointResult.duties.forEach {
                                dutyWithUsersDao.delete(it.dutyId)
                                dutyDao.delete(it.dutyId)
                            }
                            dayPointDao.delete(dayPointResult.dayPoint.dayPointId)
                        }
                    }
                }.filter { !it.deleted }.forEach {

                    val dayPoint = DayPoint(
                        dayPointId = it.id,
                        title = it.title,
                        date = it.date,
                        duration = it.duration,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        typeOfDayPoint = it.typeOfDayPoint,
                        dayId = it.dayId,
                        defaultPhoto = it.defaultPhoto,
                        travelTime = it.travelTime,
                        travelType = it.travelType,
                        travelDistance = it.travelDistance,
                        openingMessage = it.openingMessage
                    )
                    dayPoint.setPhotoList(it.photoListString)
                    dayPoint.setDocumentList(it.documentListString)
                    dayPoints.add(
                        dayPoint
                    )
                }

            }
            tripDto.members.map {
                users.add(
                    User(
                        userId = it.id,
                        email = it.email,
                        name = it.name,
                        imgUrl = it.imgUrl
                    )
                )
            }

            tripDao.insetTrip(trip)
            dayDao.insetDay(*days.toTypedArray())
            dayPointDao.insetDayPoint(*dayPoints.toTypedArray())
            userDao.insertUser(*users.toTypedArray())
            users.forEach {
                tripWithUsersDao.insert(UserTripCrossRef(trip.tripId, it.userId))
            }
        }

    }

    private suspend fun saveTripInfoData(tripDto: TripInfoDto) {
        tripWithUsersAndDutyCalculationDao.deleteAll()
        dutyCalculationDao.deleteAll()

        val dayPoints = mutableListOf<DayPoint>()
        val days = mutableListOf<Day>()
        val users = mutableListOf<User>()
        val dutyCalculations = mutableListOf<DutyCalculation>()

        if(tripDto.deleted){
            deleteData(tripDto.id)
        }else {

            val trip = Trip(
                tripId = tripDto.id,
                title = tripDto.title,
                ownerId = tripDto.ownerId,
                startDay = tripDto.startDay,
                endDay = tripDto.endDay,
                defaultPhoto = tripDto.defaultPhoto,

                )
            tripDto.days.map {
                days.add(
                    Day(
                        dayId = it.id,
                        date = it.date,
                        tripId = it.tripId,
                        codeWeather = it.codeWeather,
                        minTemperature = it.minTemperature,
                        maxTemperature = it.maxTemperature
                    )
                )
            }
            tripDto.days.forEach { day ->
                day.dayPoints.onEach { dayPoint ->
                    if (dayPoint.deleted) {
                        val dayPointResult =
                            dayPointDao.findDayPointsAndDutiesAndCommentsById(dayPoint.id)
                        if (dayPointResult != null) {
                            dayPointResult.comments.forEach {
                                commentDao.delete(it.commentId)
                            }
                            dayPointResult.duties.forEach {
                                dutyWithUsersDao.delete(it.dutyId)
                                dutyDao.delete(it.dutyId)
                            }
                            dayPointDao.delete(dayPointResult.dayPoint.dayPointId)
                        }
                    }
                }.filter { !it.deleted }.forEach {

                    val dayPoint = DayPoint(
                        dayPointId = it.id,
                        title = it.title,
                        date = it.date,
                        duration = it.duration,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        typeOfDayPoint = it.typeOfDayPoint,
                        dayId = it.dayId,
                        defaultPhoto = it.defaultPhoto,
                        travelTime = it.travelTime,
                        travelType = it.travelType,
                        travelDistance = it.travelDistance,
                        openingMessage = it.openingMessage
                    )
                    dayPoint.setPhotoList(it.photoListString)
                    dayPoint.setDocumentList(it.documentListString)
                    dayPoints.add(
                        dayPoint
                    )
                }

            }
            tripDto.members.map {
                users.add(
                    User(
                        userId = it.id,
                        email = it.email,
                        name = it.name,
                        imgUrl = it.imgUrl
                    )
                )
            }
            tripDto.dutyCalculation.map {
                dutyCalculations.add(
                    DutyCalculation(
                        dutyCalculationId = null,
                        sourceUserId = it.sourceUserId,
                        targetUserId = it.targetUserId,
                        amount = it.amount,
                        currency = it.currency
                    )
                )
            }


            tripDao.insetTrip(trip)
            dayDao.insetDay(*days.toTypedArray())
            dayPointDao.insetDayPoint(*dayPoints.toTypedArray())
            userDao.insertUser(*users.toTypedArray())

            dutyCalculations.forEach {
                it.dutyCalculationId = dutyCalculationDao.insertDutyCalculation(it)
            }

            users.forEach {
                tripWithUsersDao.insert(UserTripCrossRef(trip.tripId, it.userId))
            }
            dutyCalculations.forEach {
                tripWithUsersAndDutyCalculationDao.insert(
                    DutyCalculationTripCrossRef(
                        trip.tripId,
                        it.dutyCalculationId!!
                    )
                )
            }
        }
    }
}