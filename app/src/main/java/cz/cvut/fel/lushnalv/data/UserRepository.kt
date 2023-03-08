package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import cz.cvut.fel.lushnalv.data.dto.response.*
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository(
    private val userDao: UserDao,
    private val appWebApi: AppWebApi,
    private val appPreferences: AppPreferences
) {

    val allFriends: LiveData<List<User>> =
        userDao.getAllFriends()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private fun insertUser(vararg user: User) {
        coroutineScope.launch(Dispatchers.IO) {
            userDao.insertUser(*user)
        }
    }

    fun deleteAll(){
        coroutineScope.launch(Dispatchers.IO) {
            userDao.deleteAll()
        }
    }

    fun persistUserDetails(userDetails: JwtResponse) {
        appPreferences.storeUserDetails(userDetails)
    }

    fun persistAccessToken(accessToken: String) {
        appPreferences.storeAccessToken(accessToken)
    }

    suspend fun resetPassword(email: String) {
        appWebApi.resetPassword(ResetPasswordRequest(email.trim()))
    }

    suspend fun updatePassword(email: String, password: String, code: Int): Boolean {
        val response = appWebApi.updatePassword(UpdatePasswordRequest(email.trim(), password, code)).blockingGet()
        insertUser(User(userId = response.id, email = response.email, name = response.name))
        return true
    }

    suspend fun signUpWithEmailAndPassword(name: String, email: String, password: String): Boolean {
        val response = appWebApi.signUp( UserRegistrationRequest(name.trim(), email.trim(), password)).blockingGet()
        insertUser(User(userId = response.id, email = response.email, name = response.name))
        return true

    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean {
        val response = appWebApi.signIn(UserLoginDto(email.trim(), password)).blockingGet()
        val user = User(userId = response.id, email = response.email, name = response.name)
        insertUser(user)
        persistUserDetails(response)
        persistAccessToken(response.accessToken)
        return true

    }

    suspend fun signUpWithGoogle(token: String): Boolean {
        val response =
            appWebApi.authorizationWithGoogle(UserAuthenticationGoogleRequest(token)).blockingGet()
        insertUser(User(userId = response.id, email = response.email, name = response.name, imgUrl = response.imgUrl))
        persistUserDetails(response)
        persistAccessToken(response.accessToken)
        return true
    }

    suspend fun getAllFriends(token: String): Boolean {
        val response =
            appWebApi.getUserFriends(token).blockingGet()
        response.forEach {
            insertUser(User(userId = it.id, email = it.email, name = it.name, imgUrl = it.imgUrl))
        }
        return true
    }
}