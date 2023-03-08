package cz.cvut.fel.lushnalv.components.craneCalendar

import java.time.YearMonth

enum class AnimationDirection {
    FORWARDS,
    BACKWARDS;

    fun isBackwards() = this == BACKWARDS
    fun isForwards() = this == FORWARDS
}

data class Month(
    val yearMonth: YearMonth,
    val weeks: List<Week>
)

data class Week(
    val number: Int,
    val yearMonth: YearMonth
)

