package cz.cvut.fel.lushnalv.ui.theme.createNewTrip

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.ui.theme.AppTheme

@Composable

fun TitleField(
    modifier: Modifier,
    title: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    localFocusManager: FocusManager,
    onImeAction: () -> Unit = {}
) {
    AppTheme() {
        OutlinedTextField(
            modifier = modifier,
            value = if (title.isNotEmpty()) {
                title
            } else {
                ""
            },
            onValueChange = onValueChange,
            label = {
                if (isValid) {
                    androidx.compose.material.Text(
                        text = stringResource(R.string.add_trip_title)
                    )
                } else {
                    androidx.compose.material.Text(
                        text = stringResource(R.string.title_error)
                    )
                }

            },
            isError = !isValid,
            singleLine = true,
            maxLines = 1,
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onImeAction()
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
}