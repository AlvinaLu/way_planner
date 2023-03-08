package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import androidx.room.*
import cz.cvut.fel.lushnalv.models.Day
import cz.cvut.fel.lushnalv.models.User

/**
 * The Data Access Object for the [User] class.
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getAllFriends(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg user: User)

    @Transaction
    @Query("SELECT * FROM user WHERE userId=:id LIMIT 1")
    fun findUserById(id: Long) : LiveData<User>

    @Query("DELETE FROM user")

    fun deleteAll()
}
