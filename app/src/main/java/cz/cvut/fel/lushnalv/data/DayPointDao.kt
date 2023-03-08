package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import cz.cvut.fel.lushnalv.models.*

/**
 * The Data Access Object for the [DayPoint] class.
 */
@Dao
interface DayPointDao {

    @Query("SELECT * FROM day_point")
    fun getAllDayPoints(): LiveData<List<DayPoint>>
    /**
     * Get day by trip_id.
     */
    @Query("SELECT * FROM day_point WHERE dayPointId= :id")
    fun getAllDayPointsByDayId(id: Long): LiveData<List<DayPoint>>

    @Query("SELECT * FROM day_point WHERE dayPointId= :id")
    fun findAllDayPointsByDayId(id: Long): List<DayPoint>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetDayPoint(vararg dayPoint: DayPoint)


    @Transaction
    @Query("SELECT * FROM day_point WHERE dayPointId=:id LIMIT 1")
    fun findDayPointsAndDutiesById(id: Long) : LiveData<DayPointWithDutiesAndComments>

    @Transaction
    @Query("SELECT * FROM day_point WHERE dayPointId=:id LIMIT 1")
    fun findDayPoint(id: Long) : DayPoint

    @Transaction
    @Query("SELECT * FROM day_point WHERE dayPointId=:id LIMIT 1")
    fun findDayPointsAndDutiesAndCommentsById(id: Long) : DayPointWithDutiesAndComments?


    /**
     * Get day by id.
     */
    @Transaction
    @Query("SELECT * FROM day_point WHERE dayPointId=:id LIMIT 1")
    fun findDayPointById(id: Long): LiveData<DayPoint>

    @Query("DELETE FROM day_point WHERE dayPointId=:id")
    fun delete(id: Long)

    @Query("DELETE FROM day_point")
    fun deleteAll()
}