package cz.cvut.fel.lushnalv.ui.theme.createNewTrip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.components.craneCalendar.CalendarUiState

@Composable
fun DatesTextField( modifier: Modifier,
                    calendarUiState: CalendarUiState,
                    showDialog: () -> Unit,
                    isValid: Boolean,
                    localFocusManager: FocusManager) {
    OutlinedTextField(
        modifier = modifier.clickable(onClick = showDialog),
        readOnly = true,
        value = if (!calendarUiState.hasSelectedDates) {
            ""
        } else {
            calendarUiState.selectedDatesFormatted
        },
        onValueChange = { },
        label = {
            if (isValid) {
                androidx.compose.material.Text(
                    text = stringResource(R.string.select_dates)
                )
            } else {
                androidx.compose.material.Text(
                    text = stringResource(R.string.date_error)
                )
            }

        },
        trailingIcon = {
            androidx.compose.material.Icon(
                modifier = Modifier.clickable(onClick = showDialog).testTag("create_trip_date_tag"),
                imageVector = Icons.Filled.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        },
        isError = !isValid,
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            localFocusManager.moveFocus(FocusDirection.Down)
        }),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            disabledTextColor = MaterialTheme.colorScheme.outline,
            placeholderColor = MaterialTheme.colorScheme.outline
        )
    )
}