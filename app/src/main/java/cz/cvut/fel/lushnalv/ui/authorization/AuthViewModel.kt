package cz.cvut.fel.lushnalv.ui.theme.authorization

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.data.*
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.utils.ResponseConstants
import cz.cvut.fel.lushnalv.utils.checkForInternet
import cz.cvut.fel.lushnalv.utils.getErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

enum class TabAuth {
    LOGIN, CREATE, FORGOT, UPDATE
}

@HiltViewModel
class AuthViewModel @Inject constructor(application: Application, appState: WayPlannerAppState) :
    ViewModel() {
    private var application: Application
    private var appPreferences: AppPreferences
    private var userRepository: UserRepository
    private var tripRepository: TripRepository
    private var appState: WayPlannerAppState

    init {
        appPreferences = AppPreferences.create(application)
        val appDb = WayPlannerDataBase.getInstance(application)
        userRepository =
            UserRepository(appDb.userDao(), appWebApi = AppWebApi.getApiService(), appPreferences)
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
        this.application = application
        this.appState = appState

    }


    private val _loadingState = MutableStateFlow(LoadingState.IDLE)
    val loadingState: StateFlow<LoadingState>
        get() = _loadingState.asStateFlow()

    private val _tabState = MutableStateFlow(TabAuth.LOGIN)
    val tabState: StateFlow<TabAuth>
        get() = _tabState.asStateFlow()

    fun changeStatus(loadingStatus: LoadingState) {
        _loadingState.value = loadingStatus
    }

    fun changeTabState(tabAuth: TabAuth) {
        _tabState.value = tabAuth
    }
    /**
     * Login with email and password
     * @param email
     * @param password
     */
    fun logInWithEmailAndPassword(email: String, password: String) {
        if (checkForInternet(application)) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        _loadingState.value = LoadingState.LOADING
                        val result = userRepository.signInWithEmailAndPassword(email, password)
                        if (result) {
                            _loadingState.value = LoadingState.LOADED
                            withContext(Dispatchers.Main) {
                                appState.navigateFromAuthToMain()
                            }
                        }
                    } catch (e: HttpException) {
                        val err = e.getErrorResponse()
                        if (err.errorCode == ResponseConstants.INVALID_USER_DATA.value) {
                            Log.e("logInWithEmailAndPassword", err.errorMessage, e)
                            _loadingState.value =
                                LoadingState.error(err.errorMessage)
                        } else {
                            Log.e("logInWithEmailAndPassword", e.message.toString(), e)
                            _loadingState.value = LoadingState.error(e.message.toString())
                        }
                    } catch (e: Exception) {
                        Log.e("logInWithEmailAndPassword", e.message.toString(), e)
                        _loadingState.value = LoadingState.error(e.message)
                    }
                }
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }

    /**
     * Sign up with email and password
     * @param name
     * @param email
     * @param password
     */
    fun signUpInWithEmailAndPassword(name: String, email: String, password: String) {
        if (checkForInternet(application)) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        _loadingState.value = LoadingState.LOADING
                        val result =
                            userRepository.signUpWithEmailAndPassword(name, email, password)
                        if (result) {
                            logInWithEmailAndPassword(email, password)
                        }
                        _loadingState.value = LoadingState.LOADED
                    } catch (e: HttpException) {
                        val err = e.getErrorResponse()
                        if (err.errorCode == ResponseConstants.INVALID_EMAIL.value || err.errorCode == ResponseConstants.EMAIL_ALREADY_EXIST.value) {
                            Log.e("signUpInWithEmailAndPassword", err.errorMessage, e)
                            _loadingState.value = LoadingState.error(err.errorMessage)
                        } else {
                            Log.e("signUpInWithEmailAndPassword", e.message.toString(), e)
                            _loadingState.value = LoadingState.error(e.message.toString())
                        }
                    } catch (e: Exception) {
                        Log.e("signUpInWithEmailAndPassword", e.message.toString(), e)
                        _loadingState.value = LoadingState.error(e.message)
                    }
                }
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }

    /**
     * Reset password and sent code to email
     * @param email
     */
    fun resetPassword(email: String) {
        if (checkForInternet(application)) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        _loadingState.value = LoadingState.LOADING
                        userRepository.resetPassword(email)
                        _tabState.value = TabAuth.UPDATE
                        _loadingState.value = LoadingState.LOADED
                    } catch (e: Exception) {
                        _loadingState.value = LoadingState.error(e.localizedMessage)
                    }
                }
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }

    fun updatePassword(email: String, password: String, code: String) {
        if (checkForInternet(application)) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        _loadingState.value = LoadingState.LOADING
                        val result = userRepository.updatePassword(email, password, code.toInt())
                        if (result) {
                            logInWithEmailAndPassword(email, password)
                        }
                        _loadingState.value = LoadingState.LOADED
                    } catch (e: Exception) {
                        _loadingState.value = LoadingState.error(e.localizedMessage)
                    }
                }
            }
        } else {
            _loadingState.value = LoadingState.error("No internet connection")
        }
    }

    /**
     * Sign up or sign in with geoogle credential
     * @param account
     */
    fun signWithCredential(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING
            withContext(Dispatchers.IO) {
                try {
                    val result = userRepository.signUpWithGoogle(account.idToken.toString())
                    if (result) {
                        _loadingState.value = LoadingState.LOADED
                        withContext(Dispatchers.Main) {
                            appState.navigateFromAuthToMain()
                        }
                    }
                } catch (e: HttpException) {
                    val err = e.getErrorResponse()
                    if (err.errorCode == ResponseConstants.INVALID_EMAIL.value || err.errorCode == ResponseConstants.USER_DOES_NOT_EXIST.value) {
                        Log.e("signWithCredential", err.errorMessage, e)
                        _loadingState.value =
                            LoadingState.error(err.errorMessage)
                    } else {
                        Log.e("signWithCredential", e.message.toString(), e)
                        _loadingState.value = LoadingState.error(e.message.toString())
                    }
                } catch (e: Exception) {
                    Log.e("signWithCredential", e.message.toString(), e)
                    _loadingState.value = LoadingState.error(e.message)
                }
            }
        }
    }
}


data class LoadingState private constructor(val status: Status, val msg: String? = null) {
    companion object {
        val LOADED = LoadingState(Status.SUCCESS)
        val IDLE = LoadingState(Status.IDLE)
        val LOADING = LoadingState(Status.RUNNING)
        fun error(msg: String?) = LoadingState(Status.FAILED, msg)
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED,
        IDLE,
    }
}

class AuthViewModelFactory(val application: Application, val appState: WayPlannerAppState) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(application, appState) as T
    }
}