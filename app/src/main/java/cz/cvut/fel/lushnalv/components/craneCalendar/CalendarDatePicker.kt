package cz.cvut.fel.lushnalv.components.calendar_toDelete

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import cz.cvut.fel.lushnalv.ui.theme.createNewTrip.CreateTripViewModel
import java.time.LocalDate

@Composable
fun CalendarDatePicker(
    startDate: LocalDate = LocalDate.now(),
    minDate: LocalDate = LocalDate.MIN,
    maxDate: LocalDate = LocalDate.MAX,
    onDone: (millis: LocalDate) -> Unit,
    onDismiss: () -> Unit,
    viewModel: CreateTripViewModel
) {
    val selectedDate = remember { mutableStateOf(startDate) }
    val calendarState = remember {
        viewModel.calendarState
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(modifier = Modifier.testTag("create_trip_ok_button_tag"), onClick = {
                onDone(selectedDate.value)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        },
        text = {
            cz.cvut.fel.lushnalv.components.craneCalendar.CalendarContent(
                calendarState = calendarState,
                onDayClicked = { dateClicked ->
                   viewModel.onDaySelected(dateClicked)
                },
            )
        }
    )
}