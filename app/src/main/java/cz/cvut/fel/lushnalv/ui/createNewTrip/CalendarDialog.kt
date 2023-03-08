package cz.cvut.fel.lushnalv.ui.theme.createNewTrip

import androidx.compose.runtime.Composable
import cz.cvut.fel.lushnalv.components.calendar_toDelete.CalendarDatePicker
import java.time.LocalDate

@Composable
fun CalendarDialog(
    onDaySave: (millis: LocalDate)  -> Unit,
    onDayCancel: () -> Unit,
    viewModel: CreateTripViewModel
) {
    CalendarDatePicker(
    startDate = LocalDate.now(),
    minDate = LocalDate.now().minusMonths(1),
    maxDate = LocalDate.now().plusYears(20L),
    onDone = onDaySave,
    onDismiss = onDayCancel,
    viewModel = viewModel
    )
}