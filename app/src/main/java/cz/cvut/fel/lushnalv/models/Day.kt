package cz.cvut.fel.lushnalv.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "day")
data class Day(
    @PrimaryKey @ColumnInfo(name = "dayId")
    var dayId: Long = 0,
    var date: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "tripId")
    var tripId: Long = 0,
    var codeWeather: Int,
    var minTemperature: Double,
    var maxTemperature: Double,
)