package cz.cvut.fel.lushnalv.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.data.measure
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.mapScreen.MapScreenModelView

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    name: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    getPLacesBySearch: (Int, LatLng) -> Unit,
    searchQueryClear: () -> Unit,
    localFocusManager: FocusManager,
    viewModel: MapScreenModelView,
    cameraPositionState: CameraPositionState
) {

    AppTheme {

        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = name,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    modifier= Modifier.clickable(onClick = {
                        getPLacesBySearch(measure(cameraPositionState), cameraPositionState.position.target)
                        localFocusManager.clearFocus()
                    }),
                    imageVector = Icons.Filled.Search, contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            trailingIcon = {
                Icon(
                    modifier= Modifier.clickable( onClick = {
                        searchQueryClear()
                        localFocusManager.clearFocus()
                    }),
                    imageVector = Icons.Filled.Clear, contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            label = {
                if (!isValid) {
                    Text(
                        text = stringResource(R.string.search_empty_error)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.enter_search)
                    )
                }

            },
            isError = !isValid,
            singleLine = true,
            maxLines = 1,
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide();
                localFocusManager.clearFocus()
                getPLacesBySearch(measure(cameraPositionState), cameraPositionState.position.target)
            }),
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
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
