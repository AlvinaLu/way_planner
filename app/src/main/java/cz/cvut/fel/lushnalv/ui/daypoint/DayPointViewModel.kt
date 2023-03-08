package cz.cvut.fel.lushnalv.ui.daypoint

import android.app.Application
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.google.android.gms.tasks.Tasks.whenAllSuccess
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.data.*
import cz.cvut.fel.lushnalv.data.dto.response.*
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.*
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
import java.io.File
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import kotlin.collections.*


data class Album(
    var name: String = "",
    var images: MutableList<cz.cvut.fel.lushnalv.ui.daypoint.Image> = mutableListOf()

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

data class Image(
    val id: String,
    val name: String,
    val path: String,
    val width: Float,
    val height: Float,
    val time: Long,

    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as cz.cvut.fel.lushnalv.ui.daypoint.Image

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

enum class DayPointChips {
    Expenses, Files, Notes
}


data class ValidationNewDuty(
    var validList: Boolean = true,
    var listFocusedDirty: Boolean = false,
)

data class NewDuty(
    val title: String = "",
    val amount: BigDecimal? = null,
    val currency: CurrencyCode = CurrencyCode.EUR,
    var dayPointId: Long? = null,
    var users: List<Long> = mutableListOf(),
)

data class DocumentState(
    val document: String,
    var state: LoadingState = LoadingState.IDLE

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentState

        if (document != other.document) return false

        return true
    }

    override fun hashCode(): Int {
        return document.hashCode()
    }
}


class DayPointViewModel @Inject constructor(
    application: Application,
    appState: WayPlannerAppState
) : ViewModel() {

    var tripRepository: TripRepository
    var userRepository: UserRepository
    var dayPointRepository: DayPointRepository
    val dayPointById: LiveData<DayPointWithDutiesAndComments>
    val tripById: LiveData<TripWithUsers>
    val userListByDayPointId: LiveData<List<DutyWithUsers>>
    val userListByTripId: LiveData<List<DutyWithUsers>>
    private val filterId = MutableLiveData<Long?>()
    private val filterTripId = MutableLiveData<Long?>()
    private var application: Application
    private var appPreferences: AppPreferences
    private var appState: WayPlannerAppState
    val albums = MutableLiveData<List<Album>>(listOf())
    var imagesChosen = mutableStateListOf<Image>()
    private val _scrollUp = MutableLiveData(false)
    private var lastScrollIndex = 0


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
        dayPointById = Transformations.switchMap(filterId) { it ->
            if (it != null) {
                return@switchMap dayPointRepository.findDayPointWithDutiesById(it)
            } else {
                return@switchMap MutableLiveData(
                    DayPointWithDutiesAndComments(
                        DayPoint(),
                        listOf(),
                        listOf()
                    )
                )
            }
        }
        userListByDayPointId = Transformations.switchMap(filterId) { it ->
            if (it != null) {
                return@switchMap dayPointRepository.findDayPointWithDutiesAndUsersById(it)
            } else {
                return@switchMap MutableLiveData(listOf())
            }
        }
        tripById = Transformations.switchMap(filterTripId) { it ->
            if (it != null) {
                val t = tripRepository.findTripWithUsersById(it)
                return@switchMap t
            } else {
                return@switchMap MutableLiveData(
                    TripWithUsers(
                        trip = Trip(),
                        users = listOf()
                    )
                )

            }
        }
        userListByTripId = Transformations.switchMap(filterTripId) { it ->
            if (it != null) {
                return@switchMap dayPointRepository.findDayPointWithDutiesAndUsersById(it)
            } else {
                return@switchMap MutableLiveData(listOf())
            }
        }
        this.application = application
        this.appState = appState
    }

    private val _stateNewDuty = MutableStateFlow(
        NewDuty(
            users = if (appPreferences.userDetails != null) {
                mutableListOf(appPreferences.userDetails!!.id)
            } else mutableListOf()
        )
    )
    val stateNewDuty: StateFlow<NewDuty>
        get() = _stateNewDuty.asStateFlow()

    private val _stateNewDutyValidation = MutableStateFlow(ValidationNewDuty())
    val stateNewDutyValidation: StateFlow<ValidationNewDuty>
        get() = _stateNewDutyValidation.asStateFlow()

    private val _stateDocumentList = MutableStateFlow(mapOf<String, LoadingDocumentState>())
    val stateDocumentList: StateFlow<Map<String, LoadingDocumentState>>
        get() = _stateDocumentList.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.IDLE)
    val loadingState: StateFlow<LoadingState>
        get() = _loadingState.asStateFlow()

    private val _loadingStatePhoto = MutableStateFlow(LoadingState.IDLE)
    val loadingStatePhoto: StateFlow<LoadingState>
        get() = _loadingStatePhoto.asStateFlow()

    private val _downloadStatePhoto = MutableStateFlow(LoadingDocumentState.IDLE)
    val downloadStatePhoto: StateFlow<LoadingDocumentState>
        get() = _downloadStatePhoto.asStateFlow()

    private val _deletedStatePhoto = MutableStateFlow(LoadingState.IDLE)
    val deletedStatePhoto: StateFlow<LoadingState>
        get() = _deletedStatePhoto.asStateFlow()

    private val _dayPointChipState = MutableStateFlow(DayPointChips.Expenses)
    val dayPointChipState: StateFlow<DayPointChips>
        get() = _dayPointChipState.asStateFlow()

    private val _optionMenuChosen = MutableStateFlow(0)
    val optionMenuChosen: StateFlow<Int>
        get() = _optionMenuChosen.asStateFlow()

    fun changeStatus(loadingStatus: LoadingState) {
        _loadingState.value = loadingStatus
    }

    fun putDayPointId(id: Long) {
        filterId.value = id
    }

    fun putTripId(id: Long) {
        filterTripId.value = id
    }

    /**
     * Add or remove user in duty
     * @param index
     */
    fun checkedMemberChange(index: Long) {
        val list: MutableList<Long> = stateNewDuty.value.users.toMutableList()
        if (stateNewDuty.value.users.contains(index)) {
            list.remove(index)
        } else {
            list.add(index)
        }
        _stateNewDuty.value = stateNewDuty.value.copy(users = list)
        _stateNewDutyValidation.value = stateNewDutyValidation.value.copy(
            validList = _stateNewDuty.value.users.isNotEmpty(),
            listFocusedDirty = true
        )
    }

    /**
     * Turn to another dayЗщште ыусешщт
     * @param index of section
     */
    fun changeDayPointChips(index: Int) {
        if (index < DayPointChips.values().size) {
            _dayPointChipState.value = DayPointChips.values()[index]
        }
    }
    /**
     * Add or remove choosen image
     * @param image
     */
    fun cancelImagesAsSelected(image: Image) {
        if (!imagesChosen.contains(image)) {
            imagesChosen.add(image)
        } else {
            imagesChosen.remove(image)
        }
    }
    /**
     * Choose image section
     * @param index
     */
    fun putOptionMenuChosen(index: Int) {
        _optionMenuChosen.value = index
        imagesChosen = mutableStateListOf<Image>()
    }

    fun changeStatusPhoto(loadingStatus: LoadingState) {
        _loadingStatePhoto.value = loadingStatus
    }

    fun navigate(route: String) {
        appState.navigateTo(route)
    }

    fun upPress() {
        appState.upPress()
    }
    /**
     * Log out and clear data
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
     * Change default photo for trip
     * @param imageUrl url image that has been choosen
     * @param tripId trip Id
     */
    fun changeDefaultPhoto(
        imageUrl: String,
        tripId: Long
    ) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _deletedStatePhoto.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result = tripRepository.changeDefaultPhoto(
                                ChangePhotoDto(tripId = tripId, photoUrl = imageUrl),
                                appPreferences.accessToken as String,
                            )
                            _deletedStatePhoto.value = LoadingState.LOADED
                        } catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.USER_CANT_PERFORM_THIS_ACTION.value) {
                                Log.e("changeDefaultPhoto", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("changeDefaultPhoto", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("changeDefaultPhoto", e.message.toString(), e)
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
     * Change state of document list
     * @param url
     * @param state
     */
    fun changeDocumentList(url: String, state: LoadingDocumentState) {
        _stateDocumentList.value = _stateDocumentList.value
            .toMutableMap()
            .apply {
                put(url, state)
            }
            .toMap()
    }

    fun deleteDocument(
        documentUrl: String,
        dayPointId: Long
    ) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result = dayPointRepository.deleteDocument(
                                DeleteDocumentDto(
                                    dayPointId = dayPointId,
                                    url = documentUrl
                                ),
                                appPreferences.accessToken as String,
                            )
                            val child = result.url.replaceBefore(
                                "%2F",
                                ""
                            ).replaceAfterLast("?alt", "").replace("%2F", "")
                                .replace("?alt", "")
                            val ref =
                                FirebaseStorage.getInstance("gs://wayplanner.appspot.com").reference
                            val path = ref.child("files").child(child)

                            path.delete().addOnSuccessListener {
                                _loadingState.value = LoadingState.LOADED
                            }.addOnFailureListener {
                                Log.e("deleteDocument", it.message.toString(), it)
                                _loadingState.value =
                                    LoadingState.error(it.message.toString())
                            }
                            _stateDocumentList.value = _stateDocumentList.value
                                .toMutableMap()
                                .apply {
                                    remove(documentUrl)
                                }
                                .toMap()
                        } catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.USER_CANT_PERFORM_THIS_ACTION.value || err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value) {
                                Log.e("deleteDocument", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("deleteDocument", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("deleteDocument", e.message.toString(), e)
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
     * Delete image
     * @param imageUrl
     * @param dayPointId
     */
    fun deleteImage(
        imageUrl: String,
        dayPointId: Long
    ) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _deletedStatePhoto.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result = dayPointRepository.deleteImage(
                                DeleteImageDto(
                                    dayPointId = dayPointId,
                                    url = imageUrl
                                ),
                                appPreferences.accessToken as String,
                            )
                            val child = result.url.replaceBefore(
                                "%2F",
                                ""
                            ).replaceAfterLast("?alt", "").replace("%2F", "")
                                .replace("?alt", "")
                            val ref =
                                FirebaseStorage.getInstance("gs://wayplanner.appspot.com").reference
                            val path = ref.child("images").child(child)
                            path.delete().addOnSuccessListener {
                                _deletedStatePhoto.value = LoadingState.LOADED
                            }.addOnFailureListener {
                                Log.e("deleteImage", it.message.toString(), it)
                                _deletedStatePhoto.value =
                                    LoadingState.error(it.message.toString())
                            }
                        } catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.USER_CANT_PERFORM_THIS_ACTION.value || err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value) {
                                Log.e("deleteImage", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("deleteImage", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("deleteImage", e.message.toString(), e)
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
     * Download document to phone
     * @param url
     * @param name
     */
    fun downloadDocument(url: String, name: String) {
        changeDocumentList(url, LoadingDocumentState.IDLE)
        if (application != null) {
            val filePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = newFile(filePath.path, name)
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    changeDocumentList(url, LoadingDocumentState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            getFileFromStorage(file, url) {
                                val data = FileProvider.getUriForFile(
                                    application!!,
                                    "cz.cvut.fel.lushnalv",
                                    file
                                )
                                changeDocumentList(
                                    url,
                                    LoadingDocumentState.success(data.toString())
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("saveDocumentToPhone", e.message.toString(), e)
                            changeDocumentList(
                                url,
                                LoadingDocumentState.error(e.message.toString())
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Download image to phone
     * @param url
     * @param name
     */
    fun downloadImage(url: String, name: String) {
        val filePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = newFile(filePath.path, name)
        if (appPreferences.accessToken != null) {
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
        }
    }
    /**
     * create new file
     * @param path
     * @param name
     * @return new file
     */
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
     * Get file from firebase
     * @param mFile
     * @param fileUrl
     */
    private fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
        val path = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl)
        path.getFile(mFile)
            .addOnSuccessListener {
                function()
            }
            .addOnFailureListener {
                Log.e("saveDocumentToPhone", it.message.toString(), it)
                changeDocumentList(fileUrl, LoadingDocumentState.error(it.message.toString()))
            }
    }
    /**
     * Get image from firebase storage
     * @param mFile
     * @param fileUrl
     */
    private fun getImageFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
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

    /**
     * Create new expense
     * @param title name of the expense
     * @param amount amount of the expense
     * @param currency currency of the expense
     * @param dayPointId in whith dayDoint must be added expense
     */
    fun createNewExpense(
        title: String,
        amount: String,
        currency: CurrencyCode,
        dayPointId: Long
    ) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val amountInBigDecimal =
                                (amount.toDouble() / 100.toDouble()).toBigDecimal()
                            val result =
                                dayPointRepository.createNewExpense(
                                    newDuty = NewDutyDto(
                                        title = title,
                                        amountInBigDecimal,
                                        currency,
                                        dayPointId,
                                        stateNewDuty.value.users
                                    ),
                                    appPreferences.accessToken as String,
                                )
                            if (result) {
                                _stateNewDuty.value = NewDuty(
                                    users = if (appPreferences.userDetails != null) {
                                        mutableListOf(appPreferences.userDetails!!.id)
                                    } else mutableListOf()
                                )
                                _loadingState.value = LoadingState.LOADED
                            }
                        } catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.INVALID_TRIP_TITLE.value
                                || err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value
                                || err.errorCode == ResponseConstants.AMOUNT_IS_EMPTY_OR_NEGATIVE.value
                                || err.errorCode == ResponseConstants.USER_DOES_NOT_EXIST.value
                            ) {
                                Log.e("createNewExpense", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("createNewExpense", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("createNewExpense", e.message.toString(), e)
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
     * Create new comment
     * @param comment
     * @param dayPointId in with dayPoint must be added comment
     */
    fun createNewComment(
        comment: String,
        dayPointId: Long
    ) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result =
                                dayPointRepository.createNewComment(
                                    newCommentDto = NewCommentDto(dayPointId, comment),
                                    appPreferences.accessToken as String,
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
                            } else if (err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value || err.errorCode == ResponseConstants.INVALID_COMMENT.value) {
                                Log.e("createNewComment", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("createNewComment", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("createNewComment", e.message.toString(), e)
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
     * Delete comment with this id
     * @param commentId
     */
    fun deleteComment(
        commentId: Long,
    ) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result =
                                dayPointRepository.deleteComment(
                                    commentId = commentId,
                                    appPreferences.accessToken as String,
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
                            } else if (err.errorCode == ResponseConstants.USER_CANT_PERFORM_THIS_ACTION.value) {
                                Log.e("deleteComment", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("deleteComment", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("deleteComment", e.message.toString(), e)
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
     * Delete duty with this id
     */
    fun deleteDuty(
        dutyId: Long,
    ) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result =
                                dayPointRepository.deleteDuty(
                                    dutyId = dutyId,
                                    appPreferences.accessToken as String,
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
                            } else if (err.errorCode == ResponseConstants.USER_CANT_PERFORM_THIS_ACTION.value) {
                                Log.e("deleteDuty", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("deleteDuty", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("deleteDuty", e.message.toString(), e)
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
     * Upload images to firebase
     * @param dayPointId
     */
    private fun uploadImagesToFirebase(dayPointId: Long) {
        val firebase = FirebaseStorage.getInstance("gs://wayplanner.appspot.com")

        val photoList = mutableListOf<String>()

        whenAllSuccess<Uri>(imagesChosen.map { image ->
            val imagesPath =
                firebase.reference.child("/images")
                    .child(image.name + "-firebase-id-" + UUID.randomUUID().toString())
            val fileUri = Uri.fromFile(File(image.path))
            val metadata = storageMetadata {
                contentType = "image/jpg"
                setCustomMetadata("width", image.width.toString())
                setCustomMetadata("height", image.height.toString())
            }
            val uploadTask = imagesPath.putFile(fileUri, metadata)
                .addOnFailureListener {
                    _deletedStatePhoto.value = LoadingState.error(it.message)
                }
                .continueWithTask { imagesPath.downloadUrl }
                .continueWith {
                    if (it.isSuccessful) {
                        val photoUri = it.result.toString()
                        if (photoUri.isNotEmpty() && photoUri != null) {
                            photoList.add(photoUri)
                            Log.i("add", "${photoList.toString()} ${photoList.size}")
                        }
                    }
                }
            return@map uploadTask
        }).addOnSuccessListener {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    Log.i("list", "${photoList.toString()} ${photoList.size}")
                    val result =
                        dayPointRepository.dayPointAddPhotos(
                            newPhotoList = NewPhotoListDto(
                                dayPointId = dayPointId,
                                photoList = photoList
                            ),
                            appPreferences.accessToken as String,
                        )
                    if (result) {
                         imagesChosen = mutableStateListOf<Image>()
                        _optionMenuChosen.value = 0
                        _deletedStatePhoto.value = LoadingState.LOADED
                    } else {
                        _deletedStatePhoto.value = LoadingState.error("Error")
                    }
                }
            }
        }
    }

    fun uploadImages(dayPointId: Long) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                if (imagesChosen.isNotEmpty()) {
                    _deletedStatePhoto.value = LoadingState.LOADING
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            try {
                                uploadImagesToFirebase(dayPointId)
                            } catch (e: HttpException) {
                                val err = e.getErrorResponse()
                                if (e.code() == 401) {
                                    withContext(Dispatchers.Main) {
                                        logOut()
                                    }
                                } else if (err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value) {
                                    Log.e("uploadImages", err.errorMessage, e)
                                    _loadingState.value =
                                        LoadingState.error(err.errorMessage)
                                } else {
                                    Log.e("uploadImages", e.message.toString(), e)
                                    _loadingState.value = LoadingState.error(e.message.toString())
                                }
                            } catch (e: Exception) {
                                Log.e("uploadImages", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message)
                            }
                        }
                    }

                } else {
                    Log.e("DayPointUploadImages", "images list is empty")
                    _deletedStatePhoto.value = LoadingState.error("Images list is empty")
                }
            } else {
                logOut()
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }
    /**
     * Upload file to firebase
     */
    fun uploadFile(dayPointId: Long, uri: Uri) {
        val firebase = FirebaseStorage.getInstance("gs://wayplanner.appspot.com")
        _loadingState.value = LoadingState.LOADING
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            var fileName = getFileName(uri)

                            val filePath =
                                firebase.reference.child("/files")
                                    .child(
                                        fileName + "-firebase-id-" + UUID.randomUUID().toString()
                                    )

                            val metadata = storageMetadata {
                                contentType = "application/pdf"
                            }
                            val uploadTask = filePath.putFile(uri, metadata)
                            uploadTask.addOnFailureListener {
                                _loadingState.value = LoadingState.error(it.message)
                            }.addOnSuccessListener { taskSnapshot ->
                                filePath.downloadUrl.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val fileUrl = it.result.toString()
                                        if (fileUrl.isNotEmpty() && fileUrl != null) {
                                            viewModelScope.launch {
                                                withContext(Dispatchers.IO) {
                                                    val result =
                                                        dayPointRepository.dayPointAddDocument(
                                                            newDocumented = NewDocumentDto(
                                                                dayPointId,
                                                                fileUrl
                                                            ),
                                                            appPreferences.accessToken as String,
                                                        )
                                                    if (result) {
                                                        _loadingState.value = LoadingState.LOADED
                                                    } else {
                                                        _loadingState.value =
                                                            LoadingState.error("Error")
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }.addOnFailureListener { it ->
                                Log.e("uploadFile", it.message.toString(), it)
                                _loadingStatePhoto.value =
                                    LoadingState.error(it.message.toString())
                            }

                        } catch (e: HttpException) {
                            val err = e.getErrorResponse()
                            if (e.code() == 401) {
                                withContext(Dispatchers.Main) {
                                    logOut()
                                }
                            } else if (err.errorCode == ResponseConstants.INVALID_DAY_POINT_ID.value) {
                                Log.e("uploadFile", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("uploadFile", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("uploadFile", e.message.toString(), e)
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
     * Get file name if exist
     * @param uri of the file
     * @return String if exist
     */
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val resolver = application!!.applicationContext!!.contentResolver
            val cursor = resolver.query(uri, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val i = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (i >= 0) {
                    result = cursor.getString(i)
                }
                cursor.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    /**
     * Get day point whole info from server
     * @param dayPointId
     */
    fun getDayPointWeb(dayPointId: Long) {
        if (checkForInternet(application)) {
            if (appPreferences.accessToken != null) {
                viewModelScope.launch {
                    _loadingState.emit(LoadingState.LOADING)
                    withContext(Dispatchers.IO) {
                        try {
                            val result =
                                dayPointRepository.getDayPointFromWeb(
                                    dayPointId = dayPointId,
                                    appPreferences.accessToken as String,
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
                                Log.e("getDayPointWeb", err.errorMessage, e)
                                _loadingState.value =
                                    LoadingState.error(err.errorMessage)
                            } else {
                                Log.e("getDayPointWeb", e.message.toString(), e)
                                _loadingState.value = LoadingState.error(e.message.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("getDayPointWeb", e.message.toString(), e)
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
     * Get all images from phone
     */
    fun getAllImages() {
        viewModelScope.launch {
            _loadingStatePhoto.emit(LoadingState.LOADING)
            withContext(Dispatchers.IO) {
                try {
                    val imageList = ArrayList<cz.cvut.fel.lushnalv.ui.daypoint.Image>()
                    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val resolver = application!!.applicationContext!!.contentResolver

                    val cursor =
                        resolver.query(
                            uri,
                            PROJECTION,
                            null,
                            null,
                            PROJECTION[5] + " DESC"
                        )
                    if (cursor != null) {
                        cursor.moveToFirst()

                        do {
                            val name =
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                            val path =
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                            val width =
                                cursor.getFloat(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))
                            val height =
                                cursor.getFloat(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))
                            val time =
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))

                            if (name != null && path != null) {
                                val image = cz.cvut.fel.lushnalv.ui.daypoint.Image(
                                    UUID.randomUUID().toString(),
                                    name,
                                    "$path",
                                    width,
                                    height,
                                    time
                                )
                                imageList.add(image)
                            }
                        } while (cursor.moveToNext())
                        cursor.close()
                    }
                    _loadingStatePhoto.emit(LoadingState.LOADED)
                    withContext(Dispatchers.Main) {
                        albums.value = splitFolder(imageList = imageList)
                        if (albums.value?.isNotEmpty() == true && albums.value?.contains(
                                Album("Camera")
                            ) == true
                        ) {
                            _optionMenuChosen.value =
                                albums.value!!.indexOf(Album("Camera"))
                        }
                    }
                } catch (e: Exception) {
                    _loadingStatePhoto.value = LoadingState.error(e.localizedMessage)
                }
            }
        }
    }

    companion object {

        private val PROJECTION = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
    }
    /**
     * Split images by folders in phone
     * @param imageList
     * @return List<Album> list albums with images
     */
    private fun splitFolder(imageList: MutableList<cz.cvut.fel.lushnalv.ui.daypoint.Image>?): List<Album> {

        val albums: MutableList<Album> = mutableListOf()
        albums.add(Album("All Image", imageList!!))

        if (imageList.isEmpty()) {
            return albums
        }

        for (i in imageList.indices) {

            val path = imageList[i].path
            val name = getFolderName(path)

            if (name.isNotEmpty()) {
                val album = getFolder(name, albums)
                album.images.add(imageList[i])
            }
        }

        return albums
    }

    private fun getFolder(name: String, albums: MutableList<Album>): Album {
        if (albums.isNotEmpty()) {
            val size = albums.size
            for (i in 0 until size) {
                val album = albums[i]
                if (name == album.name) {
                    return album
                }
            }
        }
        val newAlbum = Album(name)
        albums.add(newAlbum)
        return newAlbum
    }

    private fun getFolderName(path: String): String {
        if (path.isNotEmpty()) {
            val strings =
                path.split(File.separator.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            if (strings.size >= 2) {
                return strings[strings.size - 2]
            }
        }

        return ""
    }

    fun downloadStatePhoto(status: LoadingDocumentState) {
        _downloadStatePhoto.value = status
    }


}

data class LoadingDocumentState private constructor(val status: Status, val msg: String? = null) {
    companion object {
        fun success(msg: String?) = LoadingDocumentState(Status.SUCCESS, msg)
        val IDLE = LoadingDocumentState(Status.IDLE)
        val LOADING = LoadingDocumentState(Status.RUNNING)
        fun error(msg: String?) = LoadingDocumentState(Status.FAILED, msg)
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED,
        IDLE,
    }
}


class DayPointViewModelFactory(
    val application: Application,
    val appState: WayPlannerAppState
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DayPointViewModel(application, appState) as T
    }
}