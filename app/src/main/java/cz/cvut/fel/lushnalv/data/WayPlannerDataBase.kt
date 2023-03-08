package cz.cvut.fel.lushnalv.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.cvut.fel.lushnalv.models.*


@Database(
    entities = [CandidatePlace::class,
        Trip::class, Day::class,
        DayPoint::class, User::class,
        UserTripCrossRef::class,
        Duty::class,
        UserDutyCrossRef::class,
        DutyCalculation::class,
        DutyCalculationTripCrossRef::class,
        Comment::class],
    version = 33,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WayPlannerDataBase : RoomDatabase() {

    abstract fun candidatePlaceDao(): CandidatePlaceDao
    abstract fun tripDao(): TripDao
    abstract fun dayDao(): DayDao
    abstract fun dayPointDao(): DayPointDao
    abstract fun userDao(): UserDao
    abstract fun dutyDao(): DutyDao
    abstract fun tripWithUsersDao(): TripWithUsersDao
    abstract fun dutyWithUsersDao(): DutyWithUsersDao
    abstract fun tripWithUsersAndDutyCalculationDao(): TripWithUsersAndDutyCalculationDao
    abstract fun dutyCalculationDao(): DutyCalculationDao
    abstract fun commentDao(): CommentDao

    fun deleteAll() {
        tripWithUsersAndDutyCalculationDao().deleteAll()
        dutyCalculationDao().deleteAll()
        commentDao().deleteAll()
        candidatePlaceDao().deleteAll()
        tripWithUsersDao().deleteAll()
        dutyWithUsersDao().deleteAll()
        dutyDao().deleteAll()
        userDao().deleteAll()
        dayPointDao().deleteAll()
        dayDao().deleteAll()
        tripDao().deleteAll()
    }


    companion object {

        private var INSTANCE: WayPlannerDataBase? = null

        fun getInstance(context: Context): WayPlannerDataBase {
            synchronized(this) {
                var instance = INSTANCE
                val converters = Converters()

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WayPlannerDataBase::class.java,
                        "way_planner_database"
                    ).fallbackToDestructiveMigration()
                        .addTypeConverter(converters)
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}