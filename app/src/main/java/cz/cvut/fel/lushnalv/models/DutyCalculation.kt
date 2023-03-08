package cz.cvut.fel.lushnalv.models

import androidx.room.*
import java.math.BigDecimal

@Entity(tableName = "duty_calculation")
class DutyCalculation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "dutyCalculationId", )
    var dutyCalculationId: Long?,
    val sourceUserId: Long,
    val targetUserId: Long,
    val amount: BigDecimal,
    val currency: CurrencyCode
)

@Entity(
    tableName = "duty_calculation_trip_cross_ref",
    primaryKeys = ["tripId", "dutyCalculationId"],
    indices = [Index("tripId"), Index("dutyCalculationId")],
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = arrayOf("tripId"),
            childColumns = arrayOf("tripId"),

            ), ForeignKey(
            entity = DutyCalculation::class,
            parentColumns = arrayOf("dutyCalculationId"),
            childColumns = arrayOf("dutyCalculationId"),

            )
    ]
)
data class DutyCalculationTripCrossRef(
    val tripId: Long,
    val dutyCalculationId: Long
)


data class TripWithUsersAndDutyCalculation(
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
    val users: List<User>,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "dutyCalculationId",
        associateBy = Junction(
            value = DutyCalculationTripCrossRef::class,
            parentColumn = "tripId",
            entityColumn = "dutyCalculationId",
        )
    )
    val dutyCalculations: List<DutyCalculation>

)