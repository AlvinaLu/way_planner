package cz.cvut.fel.lushnalv.tripScreen

import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.components.*
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.*
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointViewModel
import cz.cvut.fel.lushnalv.ui.daypoint.UsersView
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.MainSheetShape
import cz.cvut.fel.lushnalv.ui.tripInfoScreen.LoadingStateWithResponse
import cz.cvut.fel.lushnalv.ui.tripInfoScreen.TripChips
import cz.cvut.fel.lushnalv.ui.tripInfoScreen.TripInfoPhotoGallery
import cz.cvut.fel.lushnalv.ui.tripInfoScreen.TripInfoViewModel
import cz.cvut.fel.lushnalv.utils.checkForInternet
import cz.cvut.fel.lushnalv.utils.cut
import java.text.DateFormatSymbols


@Composable
fun TripInfoScreen(
    viewModel: TripInfoViewModel,
    dayPointViewModel: DayPointViewModel,
    navController: NavController,
    tripId: Long
) {
    viewModel.putTripId(tripId)
    viewModel.getTripFromWeb(tripId = tripId)
    TripInfoContainer(viewModel = viewModel, tripId = tripId, dayPointViewModel)
}

data class DocumentWithDayPoint(
    val document: String,
    val dayPointTitle: String = "",
    val dayPointId: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripInfoContainer(viewModel: TripInfoViewModel, tripId: Long, dayPointViewModel: DayPointViewModel) {
    val appPref = AppPreferences.create(LocalContext.current.applicationContext)
    val currentTrip by viewModel.tripById.observeAsState()
    val loadingState: LoadingStateWithResponse by viewModel.loadingState.collectAsState()
    val chipState: TripChips by viewModel.dayPointChipState.collectAsState()
    val tripWithUsers by viewModel.tripByIdWithUsers.observeAsState()
    val dutyCalculations by viewModel.dutiesCalculations.observeAsState()

    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value)
        currentTrip?.let { trip ->
            AddFriendDialog(setShowDialog = {
                showDialog.value = it
            }, viewModel = viewModel, tripId = trip.trip.tripId)
        }


    val documentList = mutableListOf<DocumentWithDayPoint>()

    currentTrip?.daysWithPoints?.forEach { it ->
        it.points?.forEach { point ->
            point.getDocumentList().forEach { document ->
                if (document.isNotEmpty()) {
                    documentList.add(
                        DocumentWithDayPoint(
                            document,
                            point.title,
                            point.dayPointId
                        )
                    )

                }
            }
        }
    }
    val openDialogDeleteDayPoint = remember { mutableStateOf(false) }
    if (openDialogDeleteDayPoint.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                openDialogDeleteDayPoint.value = false
            },
            title = {
                Text(modifier = Modifier.testTag("trip_info_screen_confirm_delete_trip_note_tag"),
                    text = stringResource(R.string.delete_trip_confirm))
            },
            text = {
                Text("")
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    modifier = Modifier.testTag("trip_info_screen_confirm_delete_trip_button_tag"),
                    onClick = {
                        viewModel.deleteTripFromWeb(tripId)
                        openDialogDeleteDayPoint.value = false
                    },
                ) {
                    Text(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                        text = stringResource(R.string.confirm)
                    )
                }
            },
            dismissButton = {
                androidx.compose.material3.OutlinedButton(onClick = {
                    openDialogDeleteDayPoint.value = false
                }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    AppTheme() {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag("trip_info_screen_tag"),
            topBar = {
                SmallTopAppBar(
                    title = {
                        if (currentTrip != null) {
                            Text(currentTrip!!.trip.title.cut(20))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.upPress() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        if(!checkForInternet(LocalContext.current.applicationContext)) {
                            Icon(
                                modifier = Modifier.height(24.dp).width(24.dp),
                                painter = painterResource(id = R.drawable.internet),
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "No internet connection"
                            )
                        }
                        if(tripWithUsers != null && tripWithUsers?.trip?.ownerId == appPref.userDetails?.id) {
                            IconButton(modifier = Modifier.testTag("trip_info_screen_delete_trip_button_tag"), onClick = {
                                openDialogDeleteDayPoint.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null
                                )
                            }
                        }

                    },
                    )
            },
            floatingActionButton = {
                when (chipState.ordinal) {
                    2 -> ExtendedFloatingActionButton(
                        modifier = Modifier.testTag("trip_info_screen_add_friend_button_tag"),
                        text = { Text(stringResource(R.string.add_friend)) },
                        onClick = {
                            showDialog.value = true
                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    else -> {}
                }
            }
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = MainSheetShape,
                    color = MaterialTheme.colorScheme.background,
                    shadowElevation = 3.dp,
                    tonalElevation = 3.dp
                ) {
                    Column() {
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            shadowElevation = 4.dp
                        ) {
                            TripInfoPhotoGallery(viewModel = viewModel)
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp)
                        ) {
                            if (currentTrip != null && currentTrip?.trip != null) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    text = "${currentTrip!!.trip.startDay.dayOfMonth} ${DateFormatSymbols().months[currentTrip!!.trip.startDay.monthValue - 1]} ${currentTrip!!.trip.startDay.year}  -  ${currentTrip!!.trip.endDay.dayOfMonth} ${DateFormatSymbols().months[currentTrip!!.trip.endDay.monthValue - 1]} ${currentTrip!!.trip.endDay.year}",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Divider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.75f)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            TripChips.values().forEachIndexed { index, it ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    CustomSortChip(
                                        selected = chipState == it,
                                        text = it.name,
                                        Modifier.clickable { viewModel.changeTripChips(index) }.testTag(it.name))
                                }

                            }
                        }
                        if (loadingState.status == LoadingStateWithResponse.Status.RUNNING) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            if (loadingState.status == LoadingStateWithResponse.Status.FAILED) {
                                viewModel.changeStatus(LoadingStateWithResponse.IDLE)
                                Toast.makeText(
                                    LocalContext.current,
                                    loadingState.msg ?: stringResource(R.string.error),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            if (loadingState.status == LoadingStateWithResponse.Status.SUCCESS) {
                                viewModel.changeStatus(LoadingStateWithResponse.IDLE)
                                if(loadingState.msg!=null) {
                                    Toast.makeText(LocalContext.current, loadingState.msg ?: stringResource(R.string.success), Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            when (chipState.ordinal) {
                                0 -> {
                                    if (!dutyCalculations?.dutyCalculations.isNullOrEmpty() && !dutyCalculations?.users.isNullOrEmpty()) {
                                        val listDuties = dutyCalculations!!.dutyCalculations
                                        val listUsers = dutyCalculations!!.users
                                        LazyColumn(
                                            modifier = Modifier.fillMaxHeight(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            items(count = listDuties!!.size) { dutyIndex ->
                                                val sourceUser =
                                                    listUsers.filter { it.userId == listDuties!![dutyIndex].sourceUserId }
                                                        .firstOrNull()
                                                val targetUser =
                                                    listUsers.filter { it.userId == listDuties!![dutyIndex].targetUserId }
                                                        .firstOrNull()
                                                if (sourceUser != null && targetUser != null && sourceUser.userId != targetUser.userId) {
                                                    DutyCalculationView(
                                                        listDuties!![dutyIndex],
                                                        sourceUser = sourceUser,
                                                        targetUser = targetUser
                                                    )
                                                }

                                            }
                                        }
                                    } else {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = stringResource(R.string.isnt_expense),
                                            textAlign = TextAlign.Center,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onBackground,
                                        )
                                    }
                                }
                                1 -> {
                                    val isOwner = currentTrip?.trip?.ownerId == AppPreferences.create(LocalContext.current.applicationContext).userDetails?.id
                                    if (!documentList.isNullOrEmpty()) {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxHeight(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            items(count = documentList.size) { index ->
                                                if (documentList[index].toString().isNotEmpty()) {
                                                    FileCard(documentList[index].document, viewModel = dayPointViewModel, dayPointId = documentList[index].dayPointId, isOwner = isOwner)
                                                }
                                            }
                                            item { Box(modifier = Modifier.height(72.dp)) }
                                        }
                                    } else {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = stringResource(R.string.isnt_doc),
                                            textAlign = TextAlign.Center,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onBackground,
                                        )
                                    }
                                }
                                2 -> {
                                    tripWithUsers?.let { UsersView(trip = it) }
                                }
                                else -> {}
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
fun TripInfoScreen() {
    AppTheme {
        val navController: NavController =
            NavController(context = LocalContext.current.applicationContext)

    }
}
