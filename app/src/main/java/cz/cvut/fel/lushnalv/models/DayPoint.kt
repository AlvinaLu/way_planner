package cz.cvut.fel.lushnalv.models

import androidx.room.*
import java.time.Duration
import java.time.LocalDateTime

@Entity(tableName = "day_point")
data class DayPoint(
    @PrimaryKey @ColumnInfo(name = "dayPointId")
    var dayPointId: Long = 0,
    var title: String = "",
    var date: LocalDateTime = LocalDateTime.now(),
    var duration: Duration = Duration.ZERO,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var typeOfDayPoint: TypeOfDayPoint = TypeOfDayPoint.UNKNOWN,
    @ColumnInfo(name = "dayId")
    var dayId: Long = 0L,
    var defaultPhoto: String = "",
    var photoListString: String = "",
    var documentListString: String = "",
    var travelTime: Duration = Duration.ZERO,
    var travelType: TypeOfDayPointActive = TypeOfDayPointActive.AUTO,
    var travelDistance: Int = 0,
    val openingMessage: String = "",
) {
    fun getPhotoList(): List<String> {
        return photoListString.split(",") ?: listOf()
    }

    fun setPhotoList(value: List<String>) {
        photoListString = value.joinToString(",");
    }

    fun getDocumentList(): List<String> {
        val docList = documentListString.split(",") ?: listOf()
        if (docList.isEmpty()){
            return listOf()
        }
        return docList
    }

    fun setDocumentList(value: List<String>) {
        documentListString = value.joinToString(",");
    }
}


data class DayPointWithDutiesAndComments(
    @Embedded
    var dayPoint: DayPoint,
    @Relation(
        parentColumn = "dayPointId",
        entityColumn = "dayPointId"
    )
    var duties: List<Duty>,
    @Relation(
        parentColumn = "dayPointId",
        entityColumn = "dayPointId"
    )
    var comments: List<Comment>,
)