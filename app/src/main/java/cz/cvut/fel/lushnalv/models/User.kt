package cz.cvut.fel.lushnalv.models

import androidx.room.*

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "userId")
    var userId: Long = 0,
    var email: String = "",
    var name: String = "",
    var imgUrl: String = "",
)

@Entity(
    primaryKeys = ["tripId", "userId"],
    indices = [Index("tripId"), Index("userId")],
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = arrayOf("tripId"),
            childColumns = arrayOf("tripId"),

        ), ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("userId"),
            childColumns = arrayOf("userId"),

        )
    ]
)
data class UserTripCrossRef(
    val tripId: Long,
    val userId: Long
)


data class TripWithUsers(
    @Embedded val trip: Trip,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "userId",
        associateBy = Junction(
            value = UserTripCrossRef::class,
            parentColumn = "tripId",
            entityColumn = "userId",
        )
    )
    val users: List<User>
)

