package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import androidx.room.*
import cz.cvut.fel.lushnalv.models.*
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [Trip] class.
 */
@Dao
interface TripDao {
    @Query("SELECT * FROM trip")
    fun getAllTrips(): LiveData<List<Trip>>

    @Query("SELECT * FROM trip")
    fun getAllTripsWithDaysWithPoints(): LiveData<List<TripWithDaysAndWithPoints>>

    @Query("SELECT * FROM trip")
    fun getAllTripsWithDaysWithPointsX(): List<TripWithDaysAndWithPoints>

    @Transaction
    @Query("SELECT * FROM trip")
    fun getAllTripsWithUsers(): LiveData<List<TripWithUsers>>

    @Transaction
    @Query("SELECT * FROM trip")
    fun getAllTripsWithDays(): Flow<List<TripWithDays>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetTrip(vararg trips: Trip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripWithUsers(vararg tripWithUsers: UserTripCrossRef)

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id LIMIT 1")
    fun findTripById(id: Long): LiveData<TripWithDays>

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id LIMIT 1")
    fun findSimplyTripById(id: Long): Trip?

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id LIMIT 1")
    fun findSimplyTripWithDaysAndDayPointsById(id: Long): TripWithDaysAndWithPoints?

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id LIMIT 1")
    fun findTripWithDaysAndDayPointsById(id: Long): LiveData<TripWithDaysAndWithPoints>

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id LIMIT 1")
    fun findSimplyTripWithUsersById(id: Long): TripWithUsers

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id LIMIT 1")
    fun findTripWithUsersById(id: Long): LiveData<TripWithUsers>

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id LIMIT 1")
    fun getTripWithUsers(id: Long): LiveData<TripWithUsers>

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id LIMIT 1")
    fun getTripWithUsersAndDutyCalculation(id: Long): LiveData<TripWithUsersAndDutyCalculation>

    @Query("DELETE FROM trip WHERE tripId=:id")
    fun delete(id: Long)

    @Query("DELETE FROM trip")
    fun deleteAll()
}

@Dao
interface TripWithUsersDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(join: UserTripCrossRef)

    @Transaction
    @Query("SELECT * FROM trip")
    fun getTrips(): List<TripWithUsers>

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id")
    fun getTrip(id: Long): LiveData<TripWithUsers>

    @Query("DELETE FROM usertripcrossref WHERE tripId=:id")
    fun delete(id: Long)

    @Query("DELETE FROM usertripcrossref")
    fun deleteAll()

}

@Dao
interface TripWithUsersAndDutyCalculationDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(join: DutyCalculationTripCrossRef)

    @Transaction
    @Query("SELECT * FROM trip")
    fun getTrips(): List<TripWithUsersAndDutyCalculation>

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id")
    fun getTrip(id: Long): LiveData<TripWithUsersAndDutyCalculation>

    @Transaction
    @Query("SELECT * FROM trip WHERE tripId=:id")
    fun getSimpleTrip(id: Long): TripWithUsersAndDutyCalculation?

    @Query("DELETE FROM duty_calculation_trip_cross_ref WHERE tripId=:id")
    fun delete(id: Long)

    @Query("DELETE FROM duty_calculation_trip_cross_ref")
    fun deleteAll()
}