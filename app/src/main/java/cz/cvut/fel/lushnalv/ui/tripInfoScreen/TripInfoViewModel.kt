package cz.cvut.fel.lushnalv.ui.tripInfoScreen

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.storage.FirebaseStorage
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.data.*
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.*
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointChips
import cz.cvut.fel.lushnalv.ui.daypoint.LoadingDocumentState
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
import java.io.File
import javax.inject.Inject


enum class TripChips {
    Expenses, Files, Team
}

class TripInfoViewModel @Inject constructor(
    application: Application,
    appState: WayPlannerAppState
) : ViewModel() {


    var tripRepository: TripRepository
    var userRepository: UserRepository
    var dayPointRepository: DayPointRepository
    val tripById: LiveData<TripWithDaysAndWithPoints>
    val tripByIdWithUsers: LiveData<TripWithUsers>
    val dutiesCalculations: LiveData<TripWithUsersAndDutyCalculation>
    private val filterId = MutableLiveData<Long?>()
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
        dayPointRepository = DayPointRepository(
            userDao,
            tripDao,
            dayDao,
            dayPointDao,
            dutyDao = appDb.dutyDao(),
            appDb.dutyWithUsersDao(),
            appDb.commentDao(),
            AppWebApi.getApiService()
        )
        userRepository =
            UserRepository(userDao, appWebApi = AppWebApi.getApiService(), appPreferences)

        tripByIdWithUsers = switchMap(filterId) { it ->
            if (it != null) {
                return@switchMap tripRepository.findTripWithUsersById(it)
            } else {
                return@switchMap MutableLiveData(
                    TripWithUsers(
                        trip = Trip(),
                        users = kotlin.collections.listOf()
                    )
                )

            }
        }
        tripById = switchMap(filterId) { it ->
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

        dutiesCalculations = switchMap(filterId) { it ->
            if (it != null) {
                return@switchMap tripRepository.findTripWithUsersAndDutyCalculationById(it)
            } else {
                return@switchMap MutableLiveData(
                    TripWithUsersAndDutyCalculation(
                        trip = Trip(),
                        users = listOf(),
                        dutyCalculations = listOf()
                    )
                )

            }
        }
        this.application = application
        this.appState = appState
    }

    private val _loadingState = MutableStateFlow(LoadingStateWithResponse.IDLE)
    val loadingState: StateFlow<LoadingStateWithResponse>
        get() = _loadingState.asStateFlow()

    fun changeStatus(loadingStatus: LoadingStateWithResponse) {
        _loadingState.value = loadingStatus
    }

    private val _tripChipState = MutableStateFlow(TripChips.Expenses)
    val dayPointChipState: StateFlow<TripChips>
        get() = _tripChipState.asStateFlow()

    private val _downloadStatePhoto = MutableStateFlow(LoadingDocumentState.IDLE)
    val downloadStatePhoto: StateFlow<LoadingDocumentState>
        get() = _downloadStatePhoto.asStateFlow()

    fun changeTripChips(index: Int) {
        if (index < DayPointChips.values().size) {
            _tripChipState.value = TripChips.values()[index]
        }
    }

    fun navigate(route: String) {
        appState.navigateTo(route)
    }

    fun putTripId(id: Long) {
        filterId.value = id
    }

    fun upPress() {
        appState.upPress()
    }

    fun downloadStatePhoto(status: LoadingDocumentState) {
        _downloadStatePhoto.value = status
    }
    /**
     * Download image to phone
     * @param url
     * @param name
     */
    fun downloadImage(url: String, name: String) {
        if (checkForInternet(application)) {
            val filePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = newFile(filePath.path, name)
            viewModelScope.launch {
                _downloadStatePhoto.value = LoadingDocumentState.LOADING
                withContext(Dispatchers.IO) {
                    try {
                        getImageFromStorage(file, url) {
                            val data = FileProvider.getUriForFile(
                                application!!,
                                "cz.cvut.fel.lushnalv",
                                file
                            )
                            _downloadStatePhoto.value =
                                LoadingDocumentState.success(data.toString())
                        }
                    } catch (e: Exception) {
                        Log.e("downloadImage", e.message.toString(), e)
                        _downloadStatePhoto.value =
                            LoadingDocumentState.error(e.message.toString())
                    }
                }
            }
        } else {
            _downloadStatePhoto.value = LoadingDocumentState.error("No internet connection")
        }
    }

    fun getImageFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
        val path = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl)
        path.getFile(mFile)
            .addOnSuccessListener {
                function()
            }
            .addOnFailureListener {
                Log.e("getImageFromStorage", it.message.toString(), it)
                _downloadStatePhoto.value = LoadingDocumentState.error(it.message.toString())
            }
    }

    private fun newFile(path: String, name: String): File {
        var file = File(path, name)
        var counter = 0
        while (file.exists()) {
            counter++
            if (name.contains(".")) {
                val (fileName, extension) = name.split(".")
                file = File(path, "$fileName ($counter).$extension")
            } else {
                file = File(path, "$name ($counter)")
            }
        }
        return file
    }
    /**
     * Log out and clean all data
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
     * Get trip wholw info from server
     * @param tripId
     */
    fun getTripFromWeb(tripId: Long) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
            viewModelScope.launch {
                _loadingState.emit(LoadingStateWithResponse.LOADING)
                withContext(Dispatchers.IO) {
                    try {
                        val result =
                            tripRepository.getTripInfoFromWeb(
                                appPreferences.accessToken as String,
                                tripId = tripId
                            )
                        if (result) {
                            _loadingState.value = LoadingStateWithResponse.success(null)
                        }
                    }  catch (e: HttpException) {
                        val err = e.getErrorResponse()
                        if (e.code() == 401) {
                            withContext(Dispatchers.Main) {
                                logOut()
                            }
                        } else if (err.errorCode == ResponseConstants.INVALID_TRIP_ID.value || err.errorCode == ResponseConstants.INVALID_TRIP_OWNER.value){
                            Log.e("getTripFromWeb", err.errorMessage, e)
                            _loadingState.value =
                                LoadingStateWithResponse.error(err.errorMessage)
                        } else {
                            Log.e("getTripFromWeb", e.message.toString(), e)
                            _loadingState.value = LoadingStateWithResponse.error(e.message.toString())
                        }
                    } catch (e: Exception) {
                        Log.e("getTripFromWeb", e.message.toString(), e)
                        _loadingState.value = LoadingStateWithResponse.error(e.message)
                    }
                }
            }
        }else {
                logOut()
            }
        }
    }
    /**
     * Delete trip
     * @param tripId
     */
    fun deleteTripFromWeb(tripId: Long) {
        if (checkForInternet(application)) {
        if (appPreferences.accessToken != null) {
            viewModelScope.launch {
                _loadingState.emit(LoadingStateWithResponse.LOADING)
                withContext(Dispatchers.IO) {
                    try {
                        val result =
                            tripRepository.deleteTrip(
                                token = appPreferences.accessToken as String,
                                tripId = tripId
                            )
                        if (result) {
                            _loadingState.value = LoadingStateWithResponse.success(null)
                            withContext(Dispatchers.Main) { appState.deleteTripNavigateToMain() }
                        }
                    }  catch (e: HttpException) {
                        val err = e.getErrorResponse()
                        if (e.code() == 401) {
                            withContext(Dispatchers.Main) {
                                logOut()
                            }
                        } else if (err.errorCode == ResponseConstants.USER_CANT_PERFORM_THIS_ACTION.value){
                            Log.e("deleteTripFromWeb", err.errorMessage, e)
                            _loadingState.value =
                                LoadingStateWithResponse.error(err.errorMessage)
                        } else {
                            Log.e("deleteTripFromWeb", e.message.toString(), e)
                            _loadingState.value = LoadingStateWithResponse.error(e.message.toString())
                        }
                    } catch (e: Exception) {
                        Log.e("deleteTripFromWeb", e.message.toString(), e)
                        _loadingState.value = LoadingStateWithResponse.error(e.message)
                    }
                }
            }
        }else {
            logOut()
        }
        } else {
            _loadingState.value = LoadingStateWithResponse.error("No internet connection")
        }
    }
    /**
     * Invite uset in this trip
     * @param tripId
     * @param email
     */
    fun inviteUser(tripId: Long, email: String) {
        if (checkForInternet(application)) {
        if (appPreferences.accessToken != null) {
            viewModelScope.launch {
                _loadingState.emit(LoadingStateWithResponse.LOADING)
                withContext(Dispatchers.IO) {
                    try {
                        val result =
                            tripRepository.inviteUser(
                                appPreferences.accessToken as String,
                                InviteUserDto(tripId = tripId, userEmail = email)
                            )
                        if (result) {
                            _loadingState.value = LoadingStateWithResponse.success("Success")
                        }
                    } catch (e: HttpException) {
                        val err = e.getErrorResponse()
                        if (e.code() == 401) {
                            withContext(Dispatchers.Main) {
                                logOut()
                            }
                        } else if (err.errorCode == ResponseConstants.USER_CANT_PERFORM_THIS_ACTION.value
                            || err.errorCode == ResponseConstants.INVALID_EMAIL.value ||
                            err.errorCode == ResponseConstants.INVALID_TRIP_ID.value){
                            Log.e("inviteUser", err.errorMessage, e)
                            _loadingState.value =
                                LoadingStateWithResponse.error(err.errorMessage)
                        } else {
                            Log.e("inviteUser", e.message.toString(), e)
                            _loadingState.value = LoadingStateWithResponse.error(e.message.toString())
                        }
                    } catch (e: Exception) {
                        Log.e("inviteUser", e.message.toString(), e)
                        _loadingState.value = LoadingStateWithResponse.error(e.message)
                    }
                }
            }
        }else {
            logOut()
        }
        } else {
            _loadingState.value = LoadingStateWithResponse.error("No internet connection")
        }
    }

}

data class LoadingStateWithResponse private constructor(
    val status: Status,
    val msg: String? = null
) {
    companion object {
        val IDLE = LoadingStateWithResponse(Status.IDLE)
        val LOADING = LoadingStateWithResponse(Status.RUNNING)
        fun success(msg: String?) = LoadingStateWithResponse(Status.SUCCESS, msg)
        fun error(msg: String?) = LoadingStateWithResponse(Status.FAILED, msg)
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED,
        IDLE,
    }
}

class TripMainViewModelFactory(val application: Application, val appState: WayPlannerAppState) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripInfoViewModel(application, appState) as T
    }
}
