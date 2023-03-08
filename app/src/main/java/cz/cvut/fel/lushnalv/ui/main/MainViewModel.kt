package cz.cvut.fel.lushnalv.ui.theme.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.data.AppWebApi
import cz.cvut.fel.lushnalv.data.TripRepository
import cz.cvut.fel.lushnalv.data.UserRepository
import cz.cvut.fel.lushnalv.data.WayPlannerDataBase
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.Trip
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import cz.cvut.fel.lushnalv.utils.checkForInternet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class MainViewModel @Inject constructor(application: Application, appState: WayPlannerAppState) :
    ViewModel() {

    var tripRepository: TripRepository
    var userRepository: UserRepository
    val allTrips: LiveData<List<Trip>>
    private var application: Application
    private var appPreferences: AppPreferences
    private var appState: WayPlannerAppState

    init {
        appPreferences = AppPreferences.create(application)
        val appDb = WayPlannerDataBase.getInstance(application)
        tripRepository = TripRepository(
            tripDao = appDb.tripDao(),
            dayDao = appDb.dayDao(),
            dayPointDao = appDb.dayPointDao(),
            userDao = appDb.userDao(),
            tripWithUsersDao = appDb.tripWithUsersDao(),
            tripWithUsersAndDutyCalculationDao = appDb.tripWithUsersAndDutyCalculationDao(),
            dutyCalculationDao = appDb.dutyCalculationDao(),
            appWebApi = AppWebApi.getApiService(),
            dutyDao = appDb.dutyDao(),
            dutyWithUsersDao = appDb.dutyWithUsersDao(),
            commentDao = appDb.commentDao(),
        )
        userRepository =
            UserRepository(appDb.userDao(), appWebApi = AppWebApi.getApiService(), appPreferences)
        allTrips = tripRepository.allTrips
        this.application = application
        this.appState = appState
    }

    private val _stateStatus = MutableStateFlow(LoadingState.IDLE)
    val stateStatus: StateFlow<LoadingState>
        get() = _stateStatus.asStateFlow()

    private val _stateRefresh = MutableStateFlow(false)
    val stateRefresh: StateFlow<Boolean>
        get() = _stateRefresh.asStateFlow()


    fun navigate(route: String) {
        appState.navigateTo(route)
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

    fun changeStatus(loadingStatus: LoadingState) {
        _stateStatus.value = loadingStatus
    }

    /**
     * Get all trips with short info
     */
    fun fetchAllTrips() {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _stateStatus.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result =
                                tripRepository.getTripsFromWeb(appPreferences.accessToken as String)
                            if (result) {
                                _stateStatus.value = LoadingState.LOADED
                            }
                        } catch (e: HttpException) {
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else {
                                Log.e("fetchAllTrips", e.message.toString(), e)
                                _stateStatus.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("fetchAllTrips", e.message.toString(), e)
                            _stateStatus.value = LoadingState.error(e.message)
                        }
                    }
                }
            }else{
                logOut()
            }
        }
    }


}

class MainViewModelFactory(val application: Application, val appState: WayPlannerAppState) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application, appState) as T
    }
}