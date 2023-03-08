package cz.cvut.fel.lushnalv.ui.theme.tripScreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.data.*
import cz.cvut.fel.lushnalv.data.dto.response.ChangeDayPointTimeDto
import cz.cvut.fel.lushnalv.data.dto.response.ChangeDuration
import cz.cvut.fel.lushnalv.data.dto.response.ReorderDayPointsDto
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.Trip
import cz.cvut.fel.lushnalv.models.TripWithDaysAndWithPoints
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import cz.cvut.fel.lushnalv.utils.ResponseConstants
import cz.cvut.fel.lushnalv.utils.checkForInternet
import cz.cvut.fel.lushnalv.utils.getErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

data class DayActive(
    val dayId: Long? = null,
    val dayIndex: Int = 0
)

class TripViewModel @Inject constructor(application: Application, appState: WayPlannerAppState) :
    ViewModel() {

    private var lastScrollIndex = 0
    private val _scrollUp = MutableLiveData(false)
    val scrollUp: LiveData<Boolean>
        get() = _scrollUp
    val appState: WayPlannerAppState
    private var application: Application
    private var appPreferences: AppPreferences

    var tripRepository: TripRepository
    var userRepository: UserRepository
    var dayPointRepository: DayPointRepository
    val allTrips: LiveData<List<TripWithDaysAndWithPoints>>
    val allTripsMap: LiveData<Map<Long, TripWithDaysAndWithPoints>>
    val tripById: LiveData<TripWithDaysAndWithPoints>
    private val filterId = MutableLiveData<Long?>()

    init {
        appPreferences = AppPreferences.create(application)
        val appDb = WayPlannerDataBase.getInstance(application)
        val userDao = appDb.userDao()
        val tripDao = appDb.tripDao()
        val dayDao = appDb.dayDao()
        val dayPointDao = appDb.dayPointDao()
        tripRepository = TripRepository(
            tripDao = tripDao,
            dayDao = dayDao,
            dayPointDao = dayPointDao,
            userDao = userDao,
            tripWithUsersDao = appDb.tripWithUsersDao(),
            tripWithUsersAndDutyCalculationDao = appDb.tripWithUsersAndDutyCalculationDao(),
            dutyCalculationDao = appDb.dutyCalculationDao(),
            appWebApi = AppWebApi.getApiService(),
            dutyDao = appDb.dutyDao(),
            dutyWithUsersDao = appDb.dutyWithUsersDao(),
            commentDao = appDb.commentDao(),
        )
        dayPointRepository = DayPointRepository(
            appDb.userDao(),
            appDb.tripDao(),
            appDb.dayDao(),
            appDb.dayPointDao(),
            appDb.dutyDao(),
            appDb.dutyWithUsersDao(),
            appDb.commentDao(),
            AppWebApi.getApiService()
        )
        userRepository =
            UserRepository(userDao, appWebApi = AppWebApi.getApiService(), appPreferences)


        allTrips = tripRepository.allTripsWithDaysWithPoints
        allTripsMap = Transformations.switchMap(allTrips) {
            if (it != null) {
                return@switchMap MutableLiveData(it.map { trip -> trip.trip.tripId to trip }
                    .toMap())
            } else {
                return@switchMap MutableLiveData(mapOf())
            }
        }
        tripById = Transformations.switchMap(filterId) { it ->
            if (it != null) {
                return@switchMap tripRepository.findTripWithDaysAndDayPointsById(it)
            } else {
                return@switchMap MutableLiveData(
                    TripWithDaysAndWithPoints(
                        trip = Trip(),
                        daysWithPoints = listOf()
                    )
                )

            }
        }
        this.application = application
        this.appState = appState
        this.application = application
    }


    fun navigate(route: String) {
        appState.navigateTo(route)
    }

    fun upPress() {
        appState.upPress()
    }

    private val _dayActive = MutableStateFlow(DayActive())
    val dayActive: StateFlow<DayActive>
        get() = _dayActive.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.IDLE)
    val loadingState: StateFlow<LoadingState>
        get() = _loadingState.asStateFlow()

    private val _loadingReorderItems = MutableStateFlow(LoadingState.IDLE)
    val loadingReorderItems: StateFlow<LoadingState>
        get() = _loadingReorderItems.asStateFlow()

    private val _loadingDragAndDrop = MutableStateFlow(LoadingStateDragAndDrop.IDLE)
    val loadingDragAndDrop: StateFlow<LoadingStateDragAndDrop>
        get() = _loadingDragAndDrop.asStateFlow()

    private val _reorderItems = MutableStateFlow(ReorderDayPointsDto(-1L, -1L))


    fun changeDayActive(dayActiveId: Long?, dayActiveIndex: Int) {
        _dayActive.value = dayActive.value.copy(dayId = dayActiveId, dayIndex = dayActiveIndex)
    }

    fun putTripId(id: Long) {
        filterId.value = id
    }

    fun changeStatus(loadingStatus: LoadingState) {
        _loadingState.value = loadingStatus
    }

    fun updateScrollPosition(newScrollIndex: Int) {
        if (newScrollIndex == lastScrollIndex) return
        _scrollUp.value = newScrollIndex > lastScrollIndex
        lastScrollIndex = newScrollIndex
    }

    fun logOut() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                WayPlannerDataBase.getInstance(application).deleteAll()
                appPreferences.clear()
            }
        }
        appState.navigateToAuth()
    }
    /**
     * Delete day point from trip
     * @param dayPointId
     */
    fun deleteDayPoint(
        dayPointId: Long,
    ) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            tripRepository.deleteDayPoint(
                                dayPointId = dayPointId,
                                appPreferences.accessToken as String,
                            )
                            _loadingState.value = LoadingState.LOADED
                        } catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.USER_CANT_PERFORM_THIS_ACTION.value || err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value) {
                                Log.e("deleteDayPoint", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("deleteDayPoint", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("deleteDayPoint", e.message.toString(), e)
                            _loadingState.value = LoadingState.error(e.message)
                        }

                    }
                }
            } else {
                logOut()
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }
    /**
     * Get trip with whole info
     */
    fun getTripFromWeb(tripId: Long) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result =
                                tripRepository.getTripFromWeb(
                                    appPreferences.accessToken as String,
                                    tripId = tripId
                                )
                            if (result) {
                                _loadingState.value = LoadingState.LOADED
                            }
                        } catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.INVALID_TRIP_ID.value || err.errorCode == ResponseConstants.INVALID_TRIP_OWNER.value) {
                                Log.e("getTripFromWeb", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("getTripFromWeb", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("getTripFromWeb", e.message.toString(), e)
                            _loadingState.value = LoadingState.error(e.message)
                        }

                    }
                }
            } else {
                logOut()
            }
        }
    }

    fun reorderItems(fromId: Long, toId: Long) {
        _reorderItems.value = ReorderDayPointsDto(movingId = fromId, targetBeforeId = toId)
    }
    /**
     *Reorder day points in day
     */
    fun reorderDayPoints() {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                if (_reorderItems.value.movingId != -1L && _reorderItems.value.targetBeforeId != -1L) {
                    viewModelScope.launch {
                        _loadingState.emit(LoadingState.LOADING)
                        withContext(Dispatchers.IO) {
                            val targetTime =
                                dayPointRepository.findDayPointById(_reorderItems.value.targetBeforeId).value?.date
                            if (targetTime != null) {
                                val dayPoint = dayPointRepository.findDayPointById(_reorderItems.value.movingId).value
                                if(dayPoint!=null){
                                    dayPoint.date = targetTime.minusSeconds(1L)
                                    dayPointRepository.insertDayPoint(dayPoint)
                                }
                            }
                            try {
                                val result =
                                    tripRepository.reorderDayPoints(
                                        _reorderItems.value,
                                        appPreferences.accessToken as String
                                    )
                                if (result) {
                                    _loadingState.value = LoadingState.LOADED
                                }
                            } catch (e: HttpException) {
                                val err = e.getErrorResponse()
                                if (e.code() == 401) {
                                    withContext(Dispatchers.Main) {
                                        logOut()
                                    }
                                } else if (err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value || err.errorCode == ResponseConstants.POINTS_ARE_IN_DIFFERENT_DAYS.value) {
                                    Log.e("reorderDayPoints", err.errorMessage, e)
                                    _loadingState.value =
                                        LoadingState.error(err.errorMessage)
                                } else {
                                    Log.e("reorderDayPoints", e.message.toString(), e)
                                    _loadingState.value = LoadingState.error(e.message.toString())
                                }
                            } catch (e: Exception) {
                                Log.e("reorderDayPoints", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message)
                            }
                        }
                    }
                } else {
                    Log.e("reorderDayPoints", "Error")
                    _loadingState.value = LoadingState.error("Error")
                }
            } else {
                logOut()
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }

    /**
     * Change day point duration
     */
    fun changeDayPointsDuration(dayPointId: Long, duration: Duration) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result =
                                tripRepository.changeDurationDayPoint(
                                    ChangeDuration(dayPointId, duration),
                                    appPreferences.accessToken as String
                                )
                            if (result) {
                                _loadingState.value = LoadingState.LOADED
                            }
                        } catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value) {
                                Log.e("changeDayPointsDuration", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("changeDayPointsDuration", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("changeDayPointsDuration", e.message.toString(), e)
                            _loadingState.value = LoadingState.error(e.message)
                        }
                    }
                }
            } else {
                logOut()
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }
    /**
     * Change day point start time(just for first activity)
     */
    fun changeDayPointsStartTime(dayPointId: Long, dayTime: LocalDateTime) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result =
                                tripRepository.changeStartTimeDayPoint(
                                    ChangeDayPointTimeDto(dayPointId, dayTime),
                                    appPreferences.accessToken as String
                                )
                            if (result) {
                                _loadingState.value = LoadingState.LOADED
                            }
                        } catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value || err.errorCode == ResponseConstants.DAY_POINT_IS_NOT_START_POINT.value) {
                                Log.e("changeDayPointsStartTime", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("changeDayPointsStartTime", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("changeDayPointsStartTime", e.message.toString(), e)
                            _loadingState.value = LoadingState.error(e.message)
                        }
                    }
                }
            } else {
                logOut()
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }

    data class LoadingStateDragAndDrop private constructor(
        val status: StatusDragAndDrop,
        val msg: String? = null
    ) {
        companion object {
            val LOADED = LoadingStateDragAndDrop(StatusDragAndDrop.SUCCESS)
            val IDLE = LoadingStateDragAndDrop(StatusDragAndDrop.IDLE)
        }

        enum class StatusDragAndDrop {
            IDLE,
            SUCCESS,
        }
    }


}

class TripViewModelFactory(val application: Application, val appState: WayPlannerAppState) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripViewModel(application, appState) as T
    }
}