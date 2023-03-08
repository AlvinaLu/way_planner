package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import cz.cvut.fel.lushnalv.models.*

/**
 * The Data Access Object for the [Duty] class.
 */
@Dao
interface DutyDao {

    @Query("SELECT * FROM duty")
    fun getAllDuties(): LiveData<List<Duty>>

    @Query("SELECT * FROM duty WHERE dayPointId= :id")
    fun getAllDutiesByDayId(id: Long): LiveData<List<Duty>>

    @Transaction
    @Query("SELECT * FROM duty WHERE dayPointId=:id")
    fun getAllDutiesWithUsersByDayId(id: Long): LiveData<List<DutyWithUsers>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuties(vararg duty: Duty)

    @Transaction
    @Query("SELECT * FROM duty WHERE dutyId=:id LIMIT 1")
    fun findDutiesById(id: Long): LiveData<Duty>

    @Transaction
    @Query("SELECT * FROM duty WHERE dutyId=:id LIMIT 1")
    fun findDutyById(id: Long): Duty

    @Query("DELETE FROM duty WHERE dutyId=:id")
    fun delete(id: Long)

    @Query("DELETE FROM duty")
    fun deleteAll()
}

@Dao
interface DutyWithUsersDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: UserDutyCrossRef)

    @Transaction
    @Query("SELECT * FROM duty")
    fun getDutiesWithUsers(): List<DutyWithUsers>

    @Transaction
    @Query("SELECT * FROM duty WHERE dayPointId=:id")
    fun getDutiesWithUsers(id: Long): LiveData<List<DutyWithUsers>>

    @Query("DELETE FROM userdutycrossref WHERE dutyId=:id")
    fun delete(id: Long)

    @Query("DELETE FROM userdutycrossref")
    fun deleteAll()
}
