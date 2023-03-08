package cz.cvut.fel.lushnalv.ui.theme.mapScreen

import android.app.Application
import android.util.Log
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.data.AppWebApi
import cz.cvut.fel.lushnalv.data.CandidatePlaceRepository
import cz.cvut.fel.lushnalv.data.TripRepository
import cz.cvut.fel.lushnalv.data.WayPlannerDataBase
import cz.cvut.fel.lushnalv.data.dto.response.NewDayPointDto
import cz.cvut.fel.lushnalv.data.googleFindPlacesApi.GoogleFindPlacesApi
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.CandidatePlace
import cz.cvut.fel.lushnalv.models.TypeOfDayPoint
import cz.cvut.fel.lushnalv.models.google.Status
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import cz.cvut.fel.lushnalv.utils.ResponseConstants
import cz.cvut.fel.lushnalv.utils.checkForInternet
import cz.cvut.fel.lushnalv.utils.getErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapi.google.model.PlacesDetailsStatus
import retrofit2.HttpException
import java.time.Duration
import java.util.*
import javax.inject.Inject

enum class RequestGooglePlace {
    restaurant, hotel, gas_station, tourist_attraction, custom
}

data class SearchState(
    var validSearch: Boolean = true,
    var searchQuery: String = ""
)


@HiltViewModel
class MapScreenModelView @Inject constructor(
    application: Application,
    appState: WayPlannerAppState
) : ViewModel() {

    private val repository: CandidatePlaceRepository
    val allCandidatePlaces: LiveData<List<CandidatePlace>>
    val candidatePlacesById: LiveData<CandidatePlace>
    private val filterId = MutableLiveData<String?>()
    private var application: Application
    private var googleFindPlacesApi = GoogleFindPlacesApi.getApiService()
    private var appPreferences: AppPreferences
    private var appState: WayPlannerAppState
    var tripRepository: TripRepository

    init {
        val appDb = WayPlannerDataBase.getInstance(application)
        val candidatePlaceDao = appDb.candidatePlaceDao()
        repository = CandidatePlaceRepository(candidatePlaceDao, googleFindPlacesApi)
        allCandidatePlaces = repository.allPlaces
        candidatePlacesById = Transformations.switchMap(filterId) {
            if (it != null) {
                return@switchMap repository.findCandidatePlaceByIdLiveData(it)
            } else {
                return@switchMap MutableLiveData(CandidatePlace())
            }
        }
        val tripDao = appDb.tripDao()
        val dayDao = appDb.dayDao()
        val dayPointDao = appDb.dayPointDao()
        tripRepository = TripRepository(
            tripDao = tripDao,
            dayDao = dayDao,
            dayPointDao = dayPointDao,
            userDao = appDb.userDao(),
            tripWithUsersDao = appDb.tripWithUsersDao(),
            tripWithUsersAndDutyCalculationDao = appDb.tripWithUsersAndDutyCalculationDao(),
            dutyCalculationDao = appDb.dutyCalculationDao(),
            appWebApi = AppWebApi.getApiService(),
            dutyDao = appDb.dutyDao(),
            dutyWithUsersDao = appDb.dutyWithUsersDao(),
            commentDao = appDb.commentDao(),
        )
        this.application = application
        appPreferences = AppPreferences.create(application)
        this.appState = appState
    }

    private val _loadingState = MutableStateFlow(LoadingState.IDLE)
    val loadingState: StateFlow<LoadingState>
        get() = _loadingState.asStateFlow()

    fun changeStatus(loadingStatus: LoadingState) {
        _loadingState.value = loadingStatus
    }


    fun navigate(route: String) {
        appState.navigateTo(route)
    }

    fun upPress() {
        appState.upPress()
    }


    private val _cameraPosition =
        MutableStateFlow(CameraPosition.fromLatLngZoom(LatLng(50.1112547, 14.4985546), 13.4f))
    val cameraPosition: StateFlow<CameraPosition>
        get() = _cameraPosition.asStateFlow()

    fun changeCameraPos(position: CameraPosition) {
        _cameraPosition.value =
            CameraPosition(position.target, position.zoom, position.tilt, position.bearing)
    }


    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState>
        get() = _searchState.asStateFlow()


    private val _searchIconState = MutableStateFlow(TypeOfDayPoint.START)
    val searchIconState: StateFlow<TypeOfDayPoint>
        get() = _searchIconState.asStateFlow()

    @OptIn(ExperimentalMaterialApi::class)
    var bottomSheetScaffoldState: BottomSheetScaffoldState? = null
    var coroutineScope: CoroutineScope? = null
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
     * Add new day point to trip
     * @param duration duration of activity
     * @param dayId day in that must be added activity
     * @param photoIndex photo that must be added in dayPount as default
     */
    fun addDayPoint(duration: Duration, dayId: Long, photoIndex: Int) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                if (candidatePlacesById.value != null) {
                    val photo = if (candidatePlacesById.value!!.photoList.size > photoIndex) {
                        candidatePlacesById.value!!.photoList[photoIndex]
                    } else {
                        ""
                    }
                    val newPoint = NewDayPointDto(
                        title = candidatePlacesById.value!!.name,
                        duration = duration,
                        typeOfDayPoint = candidatePlacesById.value!!.type,
                        dayId = dayId,
                        lat = candidatePlacesById.value!!.lat,
                        lng = candidatePlacesById.value!!.lng,
                        defaultPhoto = photo,
                        openingHours = candidatePlacesById.value!!.openingHours,
                    )
                    viewModelScope.launch {
                        _loadingState.emit(LoadingState.LOADING)
                        withContext(Dispatchers.IO) {
                            try {
                                val result =
                                    tripRepository.createNewDayPoint(
                                        newPoint,
                                        appPreferences.accessToken as String
                                    )
                                if (result) {
                                    withContext(Dispatchers.Main) {
                                        _loadingState.value = LoadingState.LOADED
                                        appState.upPress()
                                    }

                                }
                            } catch (e: HttpException) {
                                val err = e.getErrorResponse()
                                if (e.code() == 401) {
                                    withContext(Dispatchers.Main) {
                                        logOut()
                                    }
                                } else if (err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value){
                                    Log.e("addDayPoint", err.errorMessage, e)
                                    _loadingState.value =
                                        LoadingState.error(err.errorMessage)
                                } else {
                                    Log.e("addDayPoint", e.message.toString(), e)
                                    _loadingState.value = LoadingState.error(e.message.toString())
                                }
                            } catch (e: Exception) {
                                Log.e("addDayPoint", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message)
                            }
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
     * Add castom activity from map
     * @param title of new activity
     * @param typeOfDayPoint type of new activity
     * @param duration duration of new activity
     * @param dayId in that must be added new activity
     * @return response entity with comment
     */
    fun addDayPointFromUnKnownSource(
        title: String,
        typeOfDayPoint: TypeOfDayPoint,
        duration: Duration,
        dayId: Long
    ) {
        if (checkForInternet(application)) {
        if (appPreferences.accessToken != null) {
            if (candidatePlacesById.value != null) {
                val newPoint = NewDayPointDto(
                    title = title,
                    duration = duration,
                    typeOfDayPoint = typeOfDayPoint,
                    dayId = dayId,
                    lat = _cameraPosition.value.target.latitude,
                    lng = _cameraPosition.value.target.longitude,
                    defaultPhoto = ""
                )
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result =
                                tripRepository.createNewDayPoint(
                                    newPoint,
                                    appPreferences.accessToken as String
                                )
                            if (result) {
                                withContext(Dispatchers.Main) {
                                    _loadingState.value = LoadingState.LOADED
                                    appState.upPress()
                                }

                            }
                        }catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value){
                                Log.e("addDayPointFromUnKnownSource", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("addDayPointFromUnKnownSource", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("addDayPointFromUnKnownSource", e.message.toString(), e)
                            _loadingState.value = LoadingState.error(e.message)
                        }
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



    fun changeSearchIconState(typeOfDayPoint: TypeOfDayPoint) {
        _searchIconState.value = typeOfDayPoint
    }

    fun searchQueryChange(search: String) {
        _searchState.value =
            searchState.value.copy(validSearch = search.isNotEmpty(), searchQuery = search.trim())
    }

    fun searchQueryClear() {
        _searchState.value =
            searchState.value.copy(validSearch = true, searchQuery = "")
    }

    fun changeTypeToNotLoaded() {
        _loadingState.value = LoadingState.IDLE
    }
    /**
     * Get places from google by search request
     * @param radius of search
     * @param center central point for search
     */
    fun getPLacesBySearch(radius: Int, center: LatLng) {
        filterId.value = null
        _searchState.value = searchState.value.copy(
            validSearch = searchState.value.searchQuery.isNotEmpty(),
            searchQuery = searchState.value.searchQuery.trim()
        )
        closeBottomScaffold()
        if (_searchState.value.validSearch) {
            _loadingState.value = LoadingState.LOADING
            viewModelScope.launch {
                repository.deleteAllCandidates()
            }
            var result: Status
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        result = repository.getPlacesFromWebBySearch(
                            _searchState.value.searchQuery.toString(),
                            radius, center
                        )
                        if (result == Status.OK) {
                            _loadingState.value = LoadingState.LOADED
                        } else if (result == Status.ZERO_RESULTS || result == Status.NOT_FOUND) {
                            _loadingState.value =
                                LoadingState.error("Nothing came up for your query in this region, try changing the map parameters!")
                        } else {
                            _loadingState.value = LoadingState.error(result.name)
                        }
                    } catch (e: Exception) {
                        Log.e("getPLacesBySearch", e.message.toString(), e)
                        _loadingState.value = LoadingState.error(e.message)
                    }

                }
            }
        }
    }

    fun getCustomPlace(
        typeOfDayPoint: TypeOfDayPoint,
    ) {
        filterId.value = null
        closeBottomScaffold()
        _searchState.value = searchState.value.copy(validSearch = true, searchQuery = "")
        viewModelScope.launch {
            repository.deleteAllCandidates()

        }
    }
    /**
     * Open bottomSheentScaffold for edit day point
     * @param bottomSheetScaffoldState from view
     * @param coroutineScope from view
     */
    @OptIn(ExperimentalMaterialApi::class)
    fun openUnknownSourceEditWindow(
        bottomSheetScaffoldState: BottomSheetScaffoldState,
        coroutineScope: CoroutineScope
    ) {
        this.bottomSheetScaffoldState = bottomSheetScaffoldState
        this.coroutineScope = coroutineScope
        _searchState.value = searchState.value.copy(validSearch = true, searchQuery = "")
        viewModelScope.launch {
            repository.deleteAllCandidates()
            openBottomScaffold()
        }
    }

    /**
     * Get places by category
     * @param requestGooglePlace category of google place
     * @param view calculated measure
     * @param center centered point for search
     */
    fun getAllPLaces(requestGooglePlace: RequestGooglePlace, view: Int, center: LatLng) {
        filterId.value = null
        _searchState.value = searchState.value.copy(validSearch = true, searchQuery = "")
        _loadingState.value = LoadingState.LOADING
        closeBottomScaffold()
        viewModelScope.launch {
            repository.deleteAllCandidates()
        }
        var result: Status
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    result = repository.getPlacesFromWeb(requestGooglePlace, view, center)
                    if (result == Status.OK) {
                        _loadingState.value = LoadingState.LOADED
                    } else if (result == Status.ZERO_RESULTS || result == Status.NOT_FOUND) {
                        _loadingState.value =
                            LoadingState.error("Nothing came up for your query in this region, try changing the map parameters!")
                    } else {
                        _loadingState.value = LoadingState.error(result.name)
                    }
                } catch (e: Exception) {
                    Log.e("getAllPLaces", e.message.toString(), e)
                    _loadingState.value = LoadingState.error(e.message)
                }

            }
        }

    }
    /**
     * Get goodle place information
     * @param placeId google place id
     * @param typeOfDayPoint type of place
     * @param bottomSheetScaffoldState
     * @param coroutineScope
     */
    @OptIn(ExperimentalMaterialApi::class)
    fun getInfoDetails(
        placeId: String,
        typeOfDayPoint: TypeOfDayPoint,
        bottomSheetScaffoldState: BottomSheetScaffoldState,
        coroutineScope: CoroutineScope
    ) {
        this.bottomSheetScaffoldState = bottomSheetScaffoldState
        this.coroutineScope = coroutineScope
        closeBottomScaffold()
        _searchState.value = searchState.value.copy(validSearch = true, searchQuery = "")
        var response: PlacesDetailsStatus
        viewModelScope.launch {
            try {
                response =
                    repository.getPlaceDetails(placeId = placeId, type = typeOfDayPoint)
                if (response == PlacesDetailsStatus.OK) {
                    filterId.value = placeId
                    openBottomScaffold()
                }
            } catch (e: Exception) {
                println(e)
            }


        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    fun openBottomScaffold() {
        if (coroutineScope != null && bottomSheetScaffoldState != null) {
            coroutineScope!!.launch {
                if (bottomSheetScaffoldState!!.bottomSheetState.isCollapsed) {
                    bottomSheetScaffoldState!!.bottomSheetState.expand()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    fun closeBottomScaffold() {
        if (coroutineScope != null && bottomSheetScaffoldState != null) {
            coroutineScope!!.launch {
                if (!bottomSheetScaffoldState!!.bottomSheetState.isCollapsed) {
                    bottomSheetScaffoldState!!.bottomSheetState.collapse()
                }
            }
        }
    }
}


class MapScreenModelViewFactory(val application: Application, val appState: WayPlannerAppState) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapScreenModelView(application, appState) as T
    }
}