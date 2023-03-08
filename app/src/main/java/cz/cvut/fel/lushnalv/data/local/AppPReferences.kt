package cz.cvut.fel.lushnalv.data.local

import android.content.Context
import android.content.SharedPreferences
import cz.cvut.fel.lushnalv.data.dto.response.JwtResponse
import cz.cvut.fel.lushnalv.data.dto.response.UserDto

class AppPreferences private constructor() {

    private lateinit var preferences: SharedPreferences

    companion object {
        private val PREFERENCE_FILE_NAME = "APP_PREFERENCES"

        fun create(context: Context): AppPreferences {
            val appPreferences = AppPreferences()
            appPreferences.preferences = context
                .getSharedPreferences(PREFERENCE_FILE_NAME, 0)
            return appPreferences
        }
    }

    val accessToken: String?
        get() = preferences.getString("ACCESS_TOKEN", null)


    fun storeAccessToken(accessToken: String) {
        preferences.edit().putString("ACCESS_TOKEN", "Bearer " + accessToken).apply()
    }

    val userDetails: UserDto?
        get(): UserDto? {
            return if (preferences.getString("EMAIL", null)!=null) {
                UserDto(
                    id = preferences.getLong("ID", -1),
                    name = preferences.getString("USERNAME", null)!!,
                    email = preferences.getString("EMAIL", null)!!,
                    imgUrl = preferences.getString("IMGURL", null)!!,
                )
            } else {
                null
            }
        }

    fun storeUserDetails(user: JwtResponse) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putLong("ID", user.id).apply()
        editor.putString("USERNAME", user.name).apply()
        editor.putString("EMAIL", user.email).apply()
        editor.putString("IMGURL", user.imgUrl).apply()
        editor.commit()
    }

    fun clear() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}