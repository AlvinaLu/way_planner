package cz.cvut.fel.lushnalv.ui.createNewTrip

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.ui.theme.AppTheme

@Composable

fun NewFriendEmailField(
    modifier: Modifier,
    email: String,
    onValueChange: (String) -> Unit,
    addNewEmail: () -> Unit,
    isValid: Boolean,
    isFocusedDirty: Boolean,
    changeExpanded: () -> Unit
) {

    fun changeState(){
        addNewEmail()
        changeExpanded()
    }
    AppTheme() {


        Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = if (email.isNotEmpty()) {
                    email
                } else {
                    ""
                },
                onValueChange = onValueChange,
                label = {
                    if (!isValid && isFocusedDirty) {
                        Text(
                            text = stringResource(R.string.invalid_email)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.add_friends_email)
                        )
                    }
                },
                isError = !isValid && isFocusedDirty,
                singleLine = true,
                maxLines = 1,
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
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
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { changeState() }, modifier = Modifier.padding(top = 4.dp), enabled = isValid && isFocusedDirty) {
                androidx.compose.material3.Text(text = "INVITE")
            }
        }
    }

}


@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PersonCardPreview() {
    AppTheme {
        NewFriendEmailField(
            modifier= Modifier,
            email = "fffff@gmail.com",
            onValueChange = {},
            addNewEmail = {},
            isValid = true,
            isFocusedDirty = true,
            changeExpanded = {}
        )

    }
}