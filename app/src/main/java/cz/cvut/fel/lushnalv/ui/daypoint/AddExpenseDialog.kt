package cz.cvut.fel.lushnalv.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.*
import cz.cvut.fel.lushnalv.ui.daypoint.*
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.authorization.Email
import cz.cvut.fel.lushnalv.ui.theme.authorization.EmailState
import cz.cvut.fel.lushnalv.ui.theme.authorization.TextFieldError
import cz.cvut.fel.lushnalv.ui.theme.authorization.TextFieldState
import cz.cvut.fel.lushnalv.ui.tripInfoScreen.TripInfoViewModel
import java.util.*


@Composable
fun AddExpenseDialog(
    viewModel: DayPointViewModel,
    setShowDialog: (Boolean) -> Unit,
    trip: TripWithUsers,
    dayPointId: Long,
) {
    AppTheme() {
        val localFocusManager = LocalFocusManager.current
        val amountFocusRequest = remember { FocusRequester() }
        val stateNewDuty by viewModel.stateNewDuty.collectAsState()
        val validationNewDuty by viewModel.stateNewDutyValidation.collectAsState()
        val locale =
            CurrencyCode.fromString(Currency.getInstance(LocalContext.current.applicationContext.resources.configuration.locales[0]).currencyCode)
        val newDutyTitleState = remember { NewDutyTitleState() }
        val newDutyAmountState = remember { NewDutyAmountState() }
        val newDutyCurrencyCodeState = remember {
            NewDutyCurrencyCodeState()
        }
        newDutyCurrencyCodeState.text = locale.name


        Dialog(onDismissRequest = { setShowDialog(false) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                    Column(modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            modifier =  Modifier.testTag("add_expense_dialog_text_tag"),
                            text = stringResource(R.string.add_new_expense_and_participants),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 3,
                        )
                        TitleNewDuty(newDutyTitleState = newDutyTitleState, localFocusManager)
                        AmountNewDuty(
                            newDutyAmountState = newDutyAmountState,
                            modifier = Modifier.focusRequester(amountFocusRequest).testTag("add_expense_dialog_amount_tag")
                        )
                        CurrencyCodeNewDuty(
                            newDutyCurrencyCodeState = newDutyCurrencyCodeState,
                            modifier = Modifier.focusRequester(amountFocusRequest)
                        )
                        FriendsShareNewDuty(
                            trip.users,
                            stateNewDuty,
                            viewModel,
                            modifier = Modifier.focusRequester(amountFocusRequest)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            androidx.compose.material3.OutlinedButton(onClick = {
                                setShowDialog(
                                    false
                                )
                            }) {
                                Text(text = stringResource(R.string.cancel))
                            }
                            androidx.compose.material3.Button(
                                modifier = Modifier.testTag("add_expense_dialog_button_tag"),
                                onClick = {
                                    viewModel.createNewExpense(
                                        newDutyTitleState.text,
                                        newDutyAmountState.text,
                                        CurrencyCode.valueOf(newDutyCurrencyCodeState.text),
                                        dayPointId = dayPointId
                                    )
                                    setShowDialog(
                                        false
                                    )
                                },
                                enabled = newDutyTitleState.isValid && newDutyAmountState.isValid && validationNewDuty.validList,
                            ) {
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

@Composable
fun AddFriendDialog(
    viewModel: TripInfoViewModel,
    setShowDialog: (Boolean) -> Unit,
    tripId: Long
) {
    AppTheme() {
        val emailState by remember { mutableStateOf(EmailState()) }


        Dialog(onDismissRequest = { setShowDialog(false) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth().testTag("trip_info_add_friend_dialog"),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.add_new_person),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 3,
                        )
                        Email(emailState, onImeAction = {})
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            androidx.compose.material3.OutlinedButton(onClick = {
                                setShowDialog(
                                    false
                                )
                            }) {
                                Text(text = stringResource(R.string.cancel))
                            }
                            androidx.compose.material3.Button(
                                modifier = Modifier.testTag("trip_info_add_friend_dialog_button_tag"),
                                onClick = {
                                    viewModel.inviteUser(tripId, emailState.text.trim())
                                    setShowDialog(
                                        false
                                    )
                                },
                                enabled = emailState.isValid,
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                    text = stringResource(R.string.invite)
                                )
                            }
                        }


                    }
                }
            }


        }
    }
}


@Composable
fun TitleNewDuty(
    newDutyTitleState: TextFieldState = remember { NewDutyTitleState() },
    localFocusManager: FocusManager,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = newDutyTitleState.text,
        onValueChange = { newDutyTitleState.text = it },
        label = {
            if (!newDutyTitleState.showErrors()) {
                androidx.compose.material.Text(
                    text = stringResource(R.string.add_trip_title)
                )
            } else {
                androidx.compose.material.Text(
                    text = stringResource(R.string.invalid_trip_title)
                )
            }

        },
        isError = newDutyTitleState.showErrors(),
        modifier = Modifier.testTag("add_expense_dialog_title_tag")
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                newDutyTitleState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    newDutyTitleState.enableShowErrors()
                }
            },
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Text
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

    newDutyTitleState.getError()?.let { error -> TextFieldError(textError = error) }
}

@Composable
fun AmountNewDuty(
    newDutyAmountState: TextFieldState = remember { NewDutyAmountState() },
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = newDutyAmountState.text,
        onValueChange = {
            newDutyAmountState.text = if (it.startsWith("0")) {
                ""
            }else {
                it
            }
        },
        label = {
                androidx.compose.material.Text(
                    text = stringResource(R.string.currency_amount)
                )

        },
        isError = newDutyAmountState.showErrors(),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                newDutyAmountState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    newDutyAmountState.enableShowErrors()
                }
            },
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.NumberPassword
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
        ),
        visualTransformation = CurrencyAmountInputVisualTransformation(
            fixedCursorAtTheEnd = true
        ),
    )

    newDutyAmountState.getError()?.let { error -> TextFieldError(textError = error) }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CurrencyCodeNewDuty(
    newDutyCurrencyCodeState: TextFieldState = remember { NewDutyCurrencyCodeState() },
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }
    val listItems = listOf<CurrencyCode>(
        CurrencyCode.CZK,
        CurrencyCode.EUR,
        CurrencyCode.GBP,
        CurrencyCode.BAM,
        CurrencyCode.BGN,
        CurrencyCode.BYR,
        CurrencyCode.CHF,
        CurrencyCode.HUF,
        CurrencyCode.HRK,
        CurrencyCode.IMP,
        CurrencyCode.ISK,
        CurrencyCode.MKD,
        CurrencyCode.MDL,
        CurrencyCode.NOK,
        CurrencyCode.RUB,
        CurrencyCode.UAH
    )



    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = newDutyCurrencyCodeState.text,
            onValueChange = { newDutyCurrencyCodeState.text = it.toString() },
            readOnly = true,
            label = { Text(text = stringResource(R.string.currency_code)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    newDutyCurrencyCodeState.onFocusChange(focusState.isFocused)
                    if (!focusState.isFocused) {
                        newDutyCurrencyCodeState.enableShowErrors()
                    }
                },
            singleLine = true,
            maxLines = 1,
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = imeAction,
                keyboardType = KeyboardType.Text
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
                placeholderColor = MaterialTheme.colorScheme.outline,
                trailingIconColor = MaterialTheme.colorScheme.outline,
            ),
        )



        ExposedDropdownMenu(
            modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listItems.forEach { selectedOption ->
                DropdownMenuItem(modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.background),
                    onClick = {
                        imeAction
                        newDutyCurrencyCodeState.text =
                            CurrencyCode.valueOf(selectedOption.name).toString()
                        expanded = false
                    }) {
                    Text(
                        text = selectedOption.name.toString() + " " + selectedOption.description.toString(),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FriendsShareNewDuty(
    listFriends: List<User>,
    stateNewDuty: NewDuty,
    viewModel: DayPointViewModel,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {

    val itemView = @Composable { text: String ->
        Text(
            text,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.outline, shape = CircleShape)
                .padding(vertical = 3.dp, horizontal = 5.dp)
        )
    }
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    var expanded by remember { mutableStateOf(false) }
    val validationNewDuty by viewModel.stateNewDutyValidation.collectAsState()
    val appPreferences = AppPreferences.create(LocalContext.current.applicationContext)


    Box(
        modifier = modifier
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.background)
            .focusable(true, interactionSource = interactionSource)
    ) {
        Box(
            modifier = Modifier
                .zIndex(1f)
                .offset(10.dp, -8.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (validationNewDuty.listFocusedDirty && !validationNewDuty.validList) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = stringResource(R.string.list_part_cant_be_empty),
                    style = androidx.compose.material.MaterialTheme.typography.caption,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = stringResource(R.string.add_particip),
                    style = androidx.compose.material.MaterialTheme.typography.caption,
                    color = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }

        }

        Surface(
            color = MaterialTheme.colorScheme.background,
            border = if (!expanded) BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            ) else BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(5.dp),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 4.dp, end = 4.dp, start = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 0.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChipVerticalGrid(
                        spacing = 8.dp,
                        moreItemsView = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        listFriends.take(4).forEach { user ->
                            if (stateNewDuty.users.contains(user.userId)) {
                                ChipForCreateExpense(
                                    user = user,
                                    onClick = viewModel::checkedMemberChange
                                )
                            }
                        }


                    }
                    IconButton(
                        onClick = { expanded = !expanded }, modifier = Modifier
                            .weight(0.13f)
                            .padding(end = 8.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            tint = MaterialTheme.colorScheme.outline,
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(
                                if (expanded)
                                    180f
                                else
                                    360f
                            )
                        )
                    }
                }
            }
            DropdownMenu(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth(0.76f)
                    .fillMaxHeight(0.5f),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
            ) {
                listFriends.forEachIndexed { index, selectionOption ->
                    DropdownMenuItem(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        content = {
                            CheckBoxUser(
                                user = selectionOption,
                                listMember = stateNewDuty.users,
                                viewModel::checkedMemberChange,
                            )
                        },
                        onClick = {
                            expanded = false
                        },
                    )


                }
            }
        }
    }
}