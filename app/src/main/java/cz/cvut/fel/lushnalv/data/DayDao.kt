package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import androidx.room.*
import cz.cvut.fel.lushnalv.models.Day
import cz.cvut.fel.lushnalv.models.DayWithDayPoints
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [Day] class.
 */
@Dao
interface DayDao {

    /**
     * Get day by trip_id.
     */
    @Query("SELECT * FROM day WHERE tripId = :id")
    fun getAllDaysByTripId(id: Long): LiveData<List<Day>>

    @Query("SELECT * FROM day WHERE tripId = :id")
    fun findAllDaysByTripId(id: Long): List<Day>

    @Transaction
    @Query("SELECT * FROM day")
    fun getDayWithDayPoints(): Flow<List<DayWithDayPoints>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetDay(vararg days: Day)

    /**
     * Get day by id.
     */
    @Transaction
    @Query("SELECT * FROM day WHERE dayId=:id LIMIT 1")
    fun findDayById(id: Long): LiveData<Day>

    @Query("DELETE FROM day WHERE dayId=:id")
    fun delete(id: Long)

    @Query("DELETE FROM day")
    fun deleteAll()

}