package cz.cvut.fel.lushnalv.ui.theme.createNewTrip

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import cz.cvut.fel.lushnalv.Routes
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.components.craneCalendar.CalendarState
import cz.cvut.fel.lushnalv.data.*
import cz.cvut.fel.lushnalv.data.dto.response.NewTripDto
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.User
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.regex.Pattern
import javax.inject.Inject

data class ValidationCreateTrip(
    var validTitle: Boolean = true,
    var validDate: Boolean = true,
    var titleFocusedDirty: Boolean = false,
    var dateFocusedDirty: Boolean = false
)

data class NewEmail(
    var valid: Boolean,
    var email: String,
    var emailFocusedDirty: Boolean = false,
)


data class Member(
    var checked: Boolean,
    var user: User,
)

data class NewTrip(
    var title: String = "",
    var startDate: LocalDate? = null,
    var endDate: LocalDate? = null,
    var startLocation: LatLng? = null,
    var membersIdExist: List<Long> = mutableListOf(),
    var newMemberEmails: List<String> = mutableListOf()
)

class CreateTripViewModel @Inject constructor(
    application: Application,
    appState: WayPlannerAppState
) : ViewModel() {

    var tripRepository: TripRepository
    var userRepository: UserRepository
    val allFriends: LiveData<List<User>>
    private var application: Application
    private var appPreferences: AppPreferences
    private var appState: WayPlannerAppState


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
        userRepository =
            UserRepository(userDao = userDao, AppWebApi.getApiService(), appPreferences)
        allFriends = userRepository.allFriends
        this.appState = appState
        this.application = application
    }

    fun navigate(route: String) {
        appState.navigateTo(route)
    }

    fun upPress() {
        appState.upPress()
    }

    fun changeStatus(loadingStatus: LoadingState) {
        _loadingState.value = loadingStatus
    }

    fun checkedMemberChange(index: Long) {
        val list: MutableList<Long> = state.value.membersIdExist.toMutableList()
        if (state.value.membersIdExist.contains(index)) {
            list.remove(index)
        } else {
            list.add(index)
        }
        _state.value = state.value.copy(membersIdExist = list)
    }


    private val _state = MutableStateFlow(NewTrip())
    val state: StateFlow<NewTrip>
        get() = _state.asStateFlow()

    private val _newEmail = MutableStateFlow(NewEmail(true, ""))
    val newEmail: StateFlow<NewEmail>
        get() = _newEmail.asStateFlow()

    fun emailChanged(email: String) {
        val valid = isEmailValid(email)
        _newEmail.value =
            newEmail.value.copy(email = email, valid = valid, emailFocusedDirty = true)
    }

    fun addEmail() {
        val list: MutableList<String> = state.value.newMemberEmails.toMutableList()
        if (_newEmail.value.valid && _newEmail.value.emailFocusedDirty) {
            list.add(_newEmail.value.email)
        }
        _state.value = state.value.copy(newMemberEmails = list)
        _newEmail.value = newEmail.value.copy(valid = true, email = "", emailFocusedDirty = false)
    }

    fun deleteEmail(email: String) {
        val list: MutableList<String> = state.value.newMemberEmails.toMutableList()
        if (list.contains(email)) {
            list.remove(email)
        }
        _state.value = state.value.copy(newMemberEmails = list)
    }

    private val _loadingState = MutableStateFlow(LoadingState.IDLE)
    val loadingState: StateFlow<LoadingState>
        get() = _loadingState.asStateFlow()

    private val _stateValidation = MutableStateFlow(ValidationCreateTrip())
    val stateValidation: StateFlow<ValidationCreateTrip> get() = _stateValidation.asStateFlow()

    private fun titleValidation() {
        _stateValidation.value.validTitle =
            _state.value.title.isNotEmpty() && _state.value.title.length > 3
    }

    private fun dateValidation() {
        _stateValidation.value.validDate =
            _state.value.startDate != null && _state.value.endDate != null
    }

    fun titleChanged(title: String) {
        _state.value = state.value.copy(title = title)
        _stateValidation.value = stateValidation.value.copy(titleFocusedDirty = true)
        titleValidation()
    }

    private val _cameraPosition =
        MutableStateFlow(CameraPosition.fromLatLngZoom(LatLng(50.0, 14.0), 13.4f))

    fun changeCameraPos(position: CameraPosition) {
        _cameraPosition.value =
            CameraPosition(position.target, position.zoom, position.tilt, position.bearing)
    }


    var calendarState = CalendarState()
    var calendarStateTmp = CalendarState()

    fun onDaySelected(daySelected: LocalDate) {
        _stateValidation.value = stateValidation.value.copy(dateFocusedDirty = true)
        viewModelScope.launch {
            calendarState.setSelectedDay(daySelected)
            dateValidation()
        }

    }

    fun onDaySave() {
        _stateValidation.value = stateValidation.value.copy(dateFocusedDirty = true)
        viewModelScope.launch {
            calendarStateTmp = calendarState
            _state.value = state.value.copy(
                startDate = calendarState.calendarUiState.value.selectedStartDate,
                endDate = calendarState.calendarUiState.value.selectedEndDate
            )
            dateValidation()
        }
    }

    fun onDayCancel() {
        viewModelScope.launch {
            calendarState = calendarStateTmp
            _state.value = state.value.copy(
                startDate = calendarStateTmp.calendarUiState.value.selectedStartDate,
                endDate = calendarStateTmp.calendarUiState.value.selectedEndDate
            )
            dateValidation()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            calendarState.calendarUiState.value = calendarState.calendarUiState.value.copy(
                selectedEndDate = null,
                selectedStartDate = null
            )
            calendarStateTmp.calendarUiState.value = calendarStateTmp.calendarUiState.value.copy(
                selectedEndDate = null,
                selectedStartDate = null
            )
            _state.value = state.value.copy(title = "", startDate = null, endDate = null)
            _cameraPosition.value =
                CameraPosition.fromLatLngZoom(LatLng(50.1112547, 14.4985546), 13.4f)
            _stateValidation.value = stateValidation.value.copy(
                validTitle = true,
                validDate = true,
                titleFocusedDirty = false,
                dateFocusedDirty = false
            )
        }
    }

    /**
     * Log out and clear all data
     */
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
     * Get all friend(of all the accounts the user has interacted with)
     */
    fun fetchAllFriends() {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            userRepository.getAllFriends(appPreferences.accessToken as String)
                        } catch (e: HttpException) {
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else {
                                Log.e("fetchAllTrips", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("fetchAllTrips", e.message.toString(), e)
                            _loadingState.value = LoadingState.error(e.message)
                        }
                    }
                }
            } else {
                logOut()
            }
        }
    }
    /**
     * Create new trip
     */
    fun createNewTrip() {
        dateValidation()
        titleValidation()
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                if (stateValidation.value.validDate && stateValidation.value.validTitle) {
                    viewModelScope.launch {
                        _loadingState.value = LoadingState.LOADING
                        withContext(Dispatchers.IO) {
                            try {
                                val result = tripRepository.createNewTrip(
                                    NewTripDto(
                                        title = _state.value.title,
                                        startDay = LocalDateTime.of(
                                            _state.value.startDate,
                                            LocalTime.MIDNIGHT
                                        ),
                                        endDay = LocalDateTime.of(
                                            _state.value.endDate,
                                            LocalTime.MIDNIGHT
                                        ),
                                        startLocation = _cameraPosition.value.target,
                                        membersIdExist = state.value.membersIdExist,
                                        newMemberEmails = state.value.newMemberEmails.toSet()
                                    ),
                                    appPreferences.accessToken as String
                                )
                                if (result) {
                                    clearAllData()
                                    withContext(Dispatchers.Main) {
                                        appState.navigateTo(Routes.MainRoute.route)
                                    }
                                }
                                _loadingState.value = LoadingState.LOADED
                            } catch (e: HttpException) {
                                val err = e.getErrorResponse()
                                if (e.code() == 401) {
                                    withContext(Dispatchers.Main) {
                                        logOut()
                                    }
                                } else if (err.errorCode == ResponseConstants.INVALID_TRIP_TITLE.value || err.errorCode == ResponseConstants.INVALID_EMAIL.value){
                                    Log.e("createNewTrip", err.errorMessage, e)
                                    _loadingState.value =
                                        LoadingState.error(err.errorMessage)
                                } else {
                                    Log.e("createNewTrip", e.message.toString(), e)
                                    _loadingState.value = LoadingState.error(e.message.toString())
                                }
                            } catch (e: Exception) {
                                Log.e("createNewTrip", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message)
                            }
                        }
                    }
                } else {
                    _loadingState.value = LoadingState.error("Invalid title or date")
                }
            } else {
                logOut()
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }

    private val EMAIL_VALIDATION_REGEX =
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"

    private fun isEmailValid(email: String): Boolean {
        return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
    }
}

class CreateTripViewModelFactory(
    val application: Application,
    val appState: WayPlannerAppState
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateTripViewModel(application, appState) as T
    }
}
