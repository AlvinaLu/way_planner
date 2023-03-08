package cz.cvut.fel.lushnalv.ui.theme.createNewTrip

import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.components.CustomChipsField
import cz.cvut.fel.lushnalv.components.MapViewForCreateTrip
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.MainSheetShape
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import cz.cvut.fel.lushnalv.utils.checkForInternet
import java.time.LocalDate

@OptIn(
  ExperimentalPermissionsApi::class,
)
@Composable
fun CreateTripScreen(
    viewModel: CreateTripViewModel,
    permissionsState: MultiplePermissionsState,
) {
    viewModel.fetchAllFriends()
    CreateTripContainer(viewModel = viewModel, permissionsState = permissionsState)
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun CreateTripContainer(
    viewModel: CreateTripViewModel,
    permissionsState: MultiplePermissionsState,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    var isMapLoaded by remember { mutableStateOf(false) }
    val loadingState by viewModel.loadingState.collectAsState()
    var showDialog = rememberSaveable { mutableStateOf(false) }
    val state: NewTrip by viewModel.state.collectAsState()
    val stateValidation: ValidationCreateTrip by viewModel.stateValidation.collectAsState()
    val allFriends by viewModel.allFriends.observeAsState(initial = listOf())
    val localFocusManager = LocalFocusManager.current
    val (focusRequester) = FocusRequester.createRefs()
    val newEmail by viewModel.newEmail.collectAsState()

    val datesFocusRequest = remember { FocusRequester() }
    val usersFocusRequest = remember { FocusRequester() }

    if (allFriends.isEmpty()) {
        viewModel.fetchAllFriends()
    }

    fun showDialog() {
        showDialog.value = true
    }

    fun saveDates(millis: LocalDate) {
        viewModel.onDaySave()
        showDialog.value = false
    }

    fun canselDates() {
        viewModel.onDayCancel()
        showDialog.value = false
    }



    AppTheme() {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag("create_trip_screen"),
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(stringResource(R.string.create_new_trip))
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.upPress()
                            viewModel.clearAllData()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        if(!checkForInternet(LocalContext.current.applicationContext)) {
                            Icon(
                                modifier = Modifier.height(24.dp).width(24.dp),
                                painter = painterResource(id = R.drawable.internet),
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = stringResource(R.string.no_internet)
                            )
                        }
                    }
                )
            },
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                if (showDialog.value) {
                    CalendarDialog(
                        onDaySave = ::saveDates,
                        onDayCancel = ::canselDates,
                        viewModel = viewModel
                    )
                }
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    shape = MainSheetShape,
                    color = MaterialTheme.colorScheme.background,
                    shadowElevation = 3.dp,
                    tonalElevation = 3.dp
                ) {
                    if (loadingState.status == LoadingState.Status.RUNNING) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        if (loadingState.status == LoadingState.Status.FAILED) {
                            viewModel.changeStatus(LoadingState.IDLE)
                            Toast.makeText(
                                LocalContext.current,
                                loadingState.msg ?: stringResource(id = R.string.error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                TitleField(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 4.dp).testTag("create_trip_title_tag"),
                                    state.title,
                                    onValueChange = viewModel::titleChanged,
                                    stateValidation.validTitle,
                                    localFocusManager,
                                    onImeAction = { datesFocusRequest.requestFocus() }
                                )

                                Box(modifier = Modifier.weight(1f).clickable(onClick = {
                                    showDialog.value = true
                                })) {
                                    val calendarUiState =
                                        viewModel.calendarState.calendarUiState.value
                                    DatesTextField(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(start = 4.dp)
                                            .focusRequester(datesFocusRequest),
                                        calendarUiState = calendarUiState,
                                        ::showDialog,
                                        isValid = stateValidation.validDate,
                                        localFocusManager
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .align(Alignment.CenterHorizontally),
                            ) {

                                CustomChipsField(
                                    listMember = state.membersIdExist,
                                    listFriends = allFriends,
                                    checkedChange = viewModel::checkedMemberChange,
                                    newEmail = newEmail,
                                    onValueEmailChange = viewModel::emailChanged,
                                    addNewEmail = viewModel::addEmail,
                                    listNewEmails = state.newMemberEmails,
                                    deleteEmail = viewModel::deleteEmail
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 8.dp, bottom = 8.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.choose_start_loc),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                var maxHeight = 0.91F
                                if (screenHeight.value < 600) {
                                    maxHeight = 0.81F
                                }
                                ConstraintLayout(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(maxHeight)
                                ) {
                                    val (
                                        linearProgressIndicator, map,
                                    ) = createRefs()
                                    Box(modifier = Modifier
                                        .constrainAs(map) {
                                            start.linkTo(parent.start)
                                            top.linkTo(parent.top)
                                            end.linkTo(parent.end)
                                        }) {
                                        MapViewForCreateTrip(
                                            onMapLoaded = {
                                                isMapLoaded = true
                                            },
                                            permissionsState = permissionsState,
                                            getCandidateUnknownSource = {},
                                            viewModel = viewModel
                                        )
                                        if (!isMapLoaded) {
                                            androidx.compose.animation.AnimatedVisibility(
                                                modifier = Modifier
                                                    .matchParentSize(),
                                                visible = !isMapLoaded,
                                                enter = EnterTransition.None,
                                                exit = fadeOut()
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier
                                                        .background(androidx.compose.material.MaterialTheme.colors.background)
                                                        .wrapContentSize(),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }

                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {

                                    androidx.compose.material3.OutlinedButton(
                                        onClick = {
                                        viewModel.clearAllData()
                                        viewModel.upPress()
                                    }) {
                                        Text(text = stringResource(R.string.cancel))
                                    }
                                    androidx.compose.material3.Button(
                                        modifier = Modifier.testTag("create_trip_create_tag"),
                                        onClick = { viewModel.createNewTrip() },
                                        enabled = stateValidation.validTitle && stateValidation.validDate && stateValidation.dateFocusedDirty && stateValidation.titleFocusedDirty,
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                            text = stringResource(R.string.create)
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddTripScreenPreview() {
    AppTheme {
        val navController: NavController =
            NavController(context = LocalContext.current.applicationContext)

    }
}