package cz.cvut.fel.lushnalv.components.craneCalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.R
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    calendarState: CalendarState,
    onDayClicked: (LocalDate) -> Unit,
) {
    Scaffold(
        modifier = Modifier.height(500.dp),
        containerColor = Color.Transparent,
        topBar = {
            CalendarTopAppBar(calendarState)
        }
    ) { contentPadding ->
        CraneCalendar(
            calendarState = calendarState,
            onDayClicked = onDayClicked,
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun CalendarTopAppBar(calendarState: CalendarState) {
    val calendarUiState = calendarState.calendarUiState.value
    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        )
        TopAppBar(
            title = {
                Text(
                    modifier = Modifier.testTag("create_trip_calendar_tag"),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    text = if (!calendarUiState.hasSelectedDates) {
                       stringResource(R.string.select_dates)
                    } else {
                        calendarUiState.selectedDatesFormatted
                    }
                )
            },
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        )
    }
}