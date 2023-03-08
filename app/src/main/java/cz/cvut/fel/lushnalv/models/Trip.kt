package cz.cvut.fel.lushnalv.models

import androidx.room.*
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(tableName = "trip")
data class Trip(
    @PrimaryKey @ColumnInfo(name = "tripId")
    var tripId: Long = -1,
    var title: String = "",
    val ownerId: Long = -1,
    var startDay: LocalDateTime = LocalDateTime.now(),
    var endDay: LocalDateTime = LocalDateTime.now(),
    val defaultPhoto: String = "",
)


data class TripWithDays(
    @Embedded
    var trip: Trip,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId"
    )
    var days: List<Day>
)

data class TripWithDaysAndWithPoints(
    @Embedded
    var trip: Trip,
    @Relation(
        entity = Day::class,
        parentColumn = "tripId",
        entityColumn = "tripId"
    )
    var daysWithPoints: List<DayWithPoints>
)

data class DayWithPoints(
    @Embedded
    var day: Day,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "dayId"
    )
    var points: List<DayPoint>
)


data class DayWithDayPoints(
    @Embedded
    val day: Day,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "dayId"
    )
    val dayPoints: List<DayPoint>
)



@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromDuration(value: Long?): Duration? {
        return value?.let { Duration.ofMillis(it) }
    }

    @TypeConverter
    fun toDuration(duration: Duration?): Long? {
        if (duration != null) {
            return duration.toMillis()
        } else {
            return null
        }
    }

    @TypeConverter
    fun bigDecimalToDouble(input: BigDecimal?) : Double {
        return input?.toDouble() ?: 0.0
    }

    @TypeConverter
    fun stringToBigDecimal(input: Double?): BigDecimal {
        if (input == null) return BigDecimal.ZERO
        return BigDecimal.valueOf(input) ?: BigDecimal.ZERO
    }
}

enum class TypeOfDayPointActive {
    AUTO, PEDESTRIAN
}

enum class TypeOfDayPoint {
    START, FOOD, HOTEL, GAS, SIGHTS, CUSTOM, UNKNOWN
}



