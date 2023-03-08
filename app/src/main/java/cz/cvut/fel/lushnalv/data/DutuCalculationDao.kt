package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import androidx.room.*
import cz.cvut.fel.lushnalv.models.DutyCalculation

@Dao
interface DutyCalculationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDutyCalculation(duty: DutyCalculation): Long

    @Transaction
    @Query("SELECT * FROM duty_calculation WHERE dutyCalculationId=:id LIMIT 1")
    fun findDutyCalculationById(id: Long) : LiveData<DutyCalculation>

    @Query("DELETE FROM duty_calculation WHERE dutyCalculationId=:id")
    fun delete(id: Long)

    @Query("DELETE FROM duty_calculation")
    fun deleteAll()
}