package cz.cvut.fel.lushnalv.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull


@Entity(tableName = "candidate_place")
data class CandidatePlace(
    @PrimaryKey()
    @NotNull
    @ColumnInfo(name = "google_id")
    val googleId: String = "",

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "add_information")
    val addInformation: String? = "",

    @ColumnInfo(name = "address")
    val address: String? = "",

    @ColumnInfo(name = "website")
    val website: String? = "",

    @ColumnInfo(name = "lat")
    val lat: Double = 0.00,

    @ColumnInfo(name = "lng")
    val lng: Double = 0.00,

    @ColumnInfo(name = "open_now")
    val openNow: Boolean? = true,

    @ColumnInfo(name = "opening_hours")
    val openingHours: String = "",

    @ColumnInfo(name = "rating")
    val rating: Double? = 0.0,

    @ColumnInfo(name = "user_ratings_total")
    val userRatingsTotal: Int? = 0,

    @ColumnInfo(name = "type")
    val type: TypeOfDayPoint = TypeOfDayPoint.START,

    @ColumnInfo(name = "photos")
    var photoListString: String = ""
){
    var photoList: List<String>
        get() {
            return photoListString?.split(",") ?: listOf()
        }
        set(value: List<String>) {
            photoListString = value.joinToString (",");
        }
}
