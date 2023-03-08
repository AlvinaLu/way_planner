package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import cz.cvut.fel.lushnalv.models.TripWithUsers

class DutyRepository(
    private val tripDao: TripDao,
    private val dayDao: DayDao,
    private val dayPointDao: DayPointDao,
    private val userDao: UserDao,
    private val tripWithUsersDao: TripWithUsersDao,
    private val dutyWithUsersDao: DutyWithUsersDao,
    private val appWebApi: AppWebApi
) {
    fun findTripWithUsers(tripId: Long): LiveData<TripWithUsers> {
        return tripDao.findTripWithUsersById(tripId)
    }
}