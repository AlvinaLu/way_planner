package cz.cvut.fel.lushnalv.utils

import cz.cvut.fel.lushnalv.components.craneCalendar.CALENDAR_STARTS_ON
import java.time.YearMonth
import java.time.temporal.WeekFields

fun YearMonth.getNumberWeeks(weekFields: WeekFields = CALENDAR_STARTS_ON): Int {
    val firstWeekNumber = this.atDay(1)[weekFields.weekOfMonth()]
    val lastWeekNumber = this.atEndOfMonth()[weekFields.weekOfMonth()]
    return lastWeekNumber - firstWeekNumber + 1 // Both weeks inclusive
}
