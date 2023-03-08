package cz.cvut.fel.lushnalv.components

import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.fel.lushnalv.*
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.models.*
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripViewModel
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripViewModelFactory
import cz.cvut.fel.lushnalv.ui.tripInfoScreen.ChangeDurationDialog
import cz.cvut.fel.lushnalv.ui.tripInfoScreen.ChangeTimeDialog
import cz.cvut.fel.lushnalv.utils.cut
import java.text.DateFormatSymbols
import java.time.Duration
import java.time.LocalDateTime


@Composable
fun DayPointCard(
    index: Int,
    dayPoint: DayPoint,
    appState: WayPlannerAppState,
    tripId: Long,
    dayActive: DayWithPoints? = null,
    viewModel: TripViewModel
) {
    val showDialogChangeDuration = remember { mutableStateOf(false) }
    val showDialogChangeStartTime = remember { mutableStateOf(false) }
    val context = LocalContext.current.applicationContext

    fun openGoogleMap(){
        val mapIntentUri =
            Uri.parse("google.navigation:q=${dayPoint.latitude},${dayPoint.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(context, mapIntent, null)
    }

    val openDialogDeleteDayPoint = remember { mutableStateOf(false) }
    if (openDialogDeleteDayPoint.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                openDialogDeleteDayPoint.value = false
            },
            title = {
                Text(text = stringResource(R.string.delete_activity_confirm))
            },
            text = {
                Text("")
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        viewModel.deleteDayPoint(dayPoint.dayPointId)
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

    if (showDialogChangeDuration.value) {
        ChangeDurationDialog(
            viewModel = viewModel,
            setShowDialog = {
                showDialogChangeDuration.value = it
            },
            dayPointId = dayPoint.dayPointId,
            duration = dayPoint.duration
        )
    }

    if (showDialogChangeStartTime.value) {
        ChangeTimeDialog(
            viewModel = viewModel,
            setShowDialog = {
                showDialogChangeStartTime.value = it
            },
            dayPointId = dayPoint.dayPointId,
            localDateTime = dayPoint.date
        )
    }

    AppTheme {
        var showMenu by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer).testTag(dayPoint.title)
                .fillMaxWidth()
                .height(64.dp)
                .clickable {
                    appState.navController.navigate(
                        Routes.DayPointRoute.createRoute(
                            tripId = tripId,
                            dayPointId = dayPoint.dayPointId
                        )
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 2.dp, bottom = 2.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            modifier = Modifier.padding(start = 8.dp),
                            shadowElevation = 2.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .width(60.dp)
                                    .height(60.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                var painter = painterResource(R.drawable.place__outline)
                                when (dayPoint.typeOfDayPoint) {
                                    TypeOfDayPoint.FOOD -> painter =
                                        painterResource(R.drawable.cafe_outline)
                                    TypeOfDayPoint.GAS -> painter =
                                        painterResource(R.drawable.local_gas_station__outline)
                                    TypeOfDayPoint.HOTEL -> painter =
                                        painterResource(R.drawable.hotel_outline)
                                    TypeOfDayPoint.SIGHTS -> painter =
                                        painterResource(R.drawable.camera_outline)
                                    else -> {
                                        painterResource(R.drawable.place__outline)
                                    }
                                }
                                Icon(
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    painter = painter, contentDescription = null
                                )
                            }
                        }
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = "${dayPoint.title.cut(30)}",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (dayActive != null && dayActive.day.date.dayOfMonth < dayPoint.date.dayOfMonth) {
                                Text(
                                    text = "${dayPoint.date.getHourString()}:${dayPoint.date.getMinuteString()} at the next day ${DateFormatSymbols().shortWeekdays[dayPoint.date.dayOfWeek.ordinal]} ${dayPoint.date.dayOfMonth} ${DateFormatSymbols().shortMonths[dayPoint.date.monthValue - 1]}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            } else {
                                Text(
                                    text = "${dayPoint.date.getHourString()}:${dayPoint.date.getMinuteString()} ",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Row(Modifier.padding(bottom = 2.dp)) {
                                Text(
                                    text = "${
                                        dayPoint.duration.toHours().getHourString()
                                    }${(dayPoint.duration.toMinutes() - (dayPoint.duration.toHours() * 60)).getMinuteString()}",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = " ${dayPoint.openingMessage}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }


                        }
                    }
                    Box(contentAlignment = Alignment.CenterEnd) {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                tint = MaterialTheme.colorScheme.outline,
                                imageVector = Icons.Filled.MoreVert, contentDescription = null
                            )
                        }
                        DropdownMenu(
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(onClick = {  showMenu = false
                                openGoogleMap()  }) {
                                Text(text = stringResource(R.string.go_to))
                            }
                            DropdownMenuItem(onClick = {  showMenu = false
                                showDialogChangeDuration.value = true }) {
                                Text(text = stringResource(R.string.change_duration))
                            }
                            if(index==0){
                                DropdownMenuItem(onClick = {   showMenu = false
                                    showDialogChangeStartTime.value = true }) {
                                    Text(text = stringResource(R.string.change_start_time))
                                }
                            }
                            DropdownMenuItem(onClick = {  showMenu = false
                                openDialogDeleteDayPoint.value = true }) {
                                Text(text = stringResource(R.string.remove))
                            }


                        }
                    }


                }

            }

        }


    }
}

@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PointCardPreview() {
    val appState = rememberWayPlannerAppState()
    val owner = LocalViewModelStoreOwner.current
    owner?.let {
        val tripViewModel: TripViewModel = viewModel(
            it,
            "TripViewModel",
            TripViewModelFactory(
                LocalContext.current.applicationContext
                        as Application, appState
            )
        )

    AppTheme {
        DayPointCard(
            0,
            DayPoint(
                title = "Start point",
                date = LocalDateTime.of(2022, 10, 25, 5, 13),
                duration = Duration.ofMinutes(3),
                typeOfDayPoint = TypeOfDayPoint.FOOD,
                dayId = 23L,
                defaultPhoto = "https://source.unsplash.com/Y4YR9OjdIMk",
                photoListString = "https://source.unsplash.com/Y4YR9OjdIMk",
                travelTime = Duration.ofMinutes(5),
                travelType = TypeOfDayPointActive.AUTO,
                travelDistance = 1000, openingMessage = "lorem"), appState,  1L,null, viewModel = tripViewModel)

    }
    }
}
