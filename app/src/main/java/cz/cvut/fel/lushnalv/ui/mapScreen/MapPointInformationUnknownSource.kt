package cz.cvut.fel.lushnalv.ui.theme.mapScreen


import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.BottomSheetShape
import cz.cvut.fel.lushnalv.ui.theme.DragHandleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.models.TypeOfDayPoint
import cz.cvut.fel.lushnalv.ui.createNewTrip.TitleNewTripState
import cz.cvut.fel.lushnalv.ui.theme.authorization.*
import java.time.Duration

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapPointInformationUnknownSource(viewModel: MapScreenModelView, dayId: Long) {
    val context = LocalContext.current
    var pickerValue by remember { mutableStateOf<Hours>(FullHours(1, 0)) }
    val options = TypeOfDayPoint.values().filter { it.name != TypeOfDayPoint.UNKNOWN.name }
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[TypeOfDayPoint.CUSTOM.ordinal]) }

    val menuFocusRequest = remember { FocusRequester() }
    val titleState = remember { TitleNewTripState() }


    AppTheme() {
        Surface(
            modifier = Modifier
                .fillMaxWidth().testTag("map_screen_bottom_sheet_tag")
                .background(MaterialTheme.colorScheme.background),
            shape = BottomSheetShape,
            color = MaterialTheme.colorScheme.background

        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Surface(
                    modifier = Modifier
                        .absolutePadding(16.dp, 16.dp, 16.dp, 0.dp)
                        .width(50.dp)
                        .height(6.dp)
                        .shadow(2.dp),
                    shape = DragHandleShape, color = MaterialTheme.colorScheme.primary,
                ) {

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {


                        Column(horizontalAlignment = Alignment.Start) {
                            val localFocusManager = LocalFocusManager.current
                            localFocusManager.moveFocus(FocusDirection.Down)

                            Title(titleState = titleState, onImeAction = { menuFocusRequest.requestFocus() })

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = {
                                    expanded = !expanded
                                },
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp).focusRequester(menuFocusRequest),
                                    readOnly = true,
                                    value = selectedOptionText.name,
                                    onValueChange = { },
                                    label = { Text(stringResource(R.string.select_type_of_activity)) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded,
                                        )
                                    },
                                    maxLines = 1,
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Text
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
                                        placeholderColor = MaterialTheme.colorScheme.outline,
                                        trailingIconColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                ExposedDropdownMenu(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                        .fillMaxWidth(),
                                    expanded = expanded,
                                    onDismissRequest = {
                                        expanded = false
                                    },
                                ) {
                                    options.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                            content = { Text(text = selectionOption.name) },
                                            onClick = {
                                                selectedOptionText = selectionOption
                                                expanded = false
                                            },
                                        )
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    text = stringResource(R.string.add_duration),
                                    color = MaterialTheme.colorScheme.outline,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                HoursNumberPicker(
                                    modifier = Modifier.fillMaxWidth(0.4f),
                                    leadingZero = true,
                                    dividersColor = MaterialTheme.colorScheme.primary,
                                    value = pickerValue,
                                    onValueChange = {
                                        pickerValue = it
                                    },
                                    hoursDivider = {
                                        Text(
                                            modifier = Modifier.size(24.dp),
                                            textAlign = TextAlign.Center,
                                            text = ":",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                                        )
                                    },
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp, start = 4.dp, bottom = 4.dp, end = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                OutlinedButton(onClick = { viewModel.upPress() }) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                                Button(modifier = Modifier.testTag("map_screen_button_add_tag"), onClick = {
                                    viewModel.addDayPointFromUnKnownSource(titleState.text, selectedOptionText, Duration.ofHours(pickerValue.hours.toLong()).plus(
                                    Duration.ofMinutes(pickerValue.minutes.toLong())), dayId = dayId) }) {
                                    Text(
                                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                        text = stringResource(R.string.add)
                                    )
                                }
                            }

                        }


                    }


                }
            }
        }
    }
}

@Composable
fun Title(
    titleState: TextFieldState = remember { TitleNewTripState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = titleState.text,
        onValueChange = { titleState.text = it },
        label = {
            if (true) {
                androidx.compose.material.Text(
                    text = stringResource(R.string.add_title)
                )
            } else {
                androidx.compose.material.Text(
                    text = stringResource(R.string.invalid_title)
                )
            }

        },
        isError = titleState.showErrors(),
        modifier = Modifier
            .fillMaxWidth().testTag("map_screen_title_tag")
            .onFocusChanged { focusState ->
                titleState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    titleState.enableShowErrors()
                }
            },
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
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

    titleState.getError()?.let { error -> TextFieldError(textError = error) }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MapPontInformationUnknownSourcePreview() {
    AppTheme {

    }
}