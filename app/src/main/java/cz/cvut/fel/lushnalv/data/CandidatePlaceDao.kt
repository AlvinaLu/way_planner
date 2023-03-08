package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import androidx.room.*
import cz.cvut.fel.lushnalv.models.CandidatePlace
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidatePlaceDao {

    @Query("SELECT * FROM candidate_place")
    fun getAllCandidatePlaces(): LiveData<List<CandidatePlace>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCandidatePlace(vararg places: CandidatePlace)

    @Update
    fun updateCandidatePlace(place: CandidatePlace)

    @Query("SELECT * FROM candidate_place WHERE google_id = :id ")
    fun findCandidatePlace(id: String) : CandidatePlace

    @Query("SELECT * FROM candidate_place WHERE google_id = :id ")
    fun findCandidatePlaceFlow(id: String) : Flow<CandidatePlace>

    @Query("SELECT * FROM candidate_place WHERE google_id = :id ")
    fun findCandidatePlaceLiveData(id: String) : LiveData<CandidatePlace>


    @Query("DELETE FROM candidate_place WHERE google_id = :id")
    fun delete(id: String)

    @Query("DELETE FROM candidate_place")
    fun deleteAll()

}