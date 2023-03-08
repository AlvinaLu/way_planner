package cz.cvut.fel.lushnalv.ui.daypoint

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.ui.theme.authorization.TextFieldError

@Composable
fun NewComment(
    newCommentState: NewCommentState = remember { NewCommentState() },
    viewModel: DayPointViewModel,
    dayPointId: Long
) {
    OutlinedTextField(
        value = newCommentState.text,
        onValueChange = {
            newCommentState.text = it
        },
        label = { Text( modifier = Modifier.testTag("add_new_note_tag"), text = stringResource(R.string.add_comment)) },
        trailingIcon = {
            androidx.compose.material.Icon(
                modifier = Modifier.testTag("add_new_note_button_tag").clickable(enabled = newCommentState.isValid, onClick = {
                    viewModel.createNewComment(newCommentState.text, dayPointId)
                    newCommentState.text = ""
                    newCommentState.isFocusedDirty = false
                }),
                tint = if(newCommentState.isValid){MaterialTheme.colorScheme.primary} else {MaterialTheme.colorScheme.outline},
                imageVector = Icons.Filled.Send,
                contentDescription = null
            )
        },
        isError = newCommentState.showErrors(),
        modifier = Modifier.testTag("add_new_note_tag")
            .fillMaxWidth().padding(8.dp)
            .onFocusChanged { focusState ->
                newCommentState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    newCommentState.enableShowErrors()
                }
            },
        singleLine = true,
        maxLines = 5,
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            disabledTextColor = MaterialTheme.colorScheme.outline,
            placeholderColor = MaterialTheme.colorScheme.outline
        ),
    )

    newCommentState.getError()?.let { error -> TextFieldError(textError = error) }
}
