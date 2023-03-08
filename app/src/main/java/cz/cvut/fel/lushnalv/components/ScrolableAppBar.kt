package cz.cvut.fel.lushnalv.components

import android.app.Application
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.Routes
import cz.cvut.fel.lushnalv.rememberWayPlannerAppState
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripImage
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripViewModel
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripViewModelFactory
import cz.cvut.fel.lushnalv.utils.checkForInternet
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableAppBar(
    tripId: Long,
    navigate: (String) -> Unit,
    viewModel: TripViewModel,
) {

    val scrollUpState = viewModel.scrollUp.observeAsState()
    val statusLoading: LoadingState by viewModel.loadingState.collectAsState()
    val currentTrip by viewModel.tripById.observeAsState()
    val dayActive = viewModel.dayActive.collectAsState()
    val statusReorderLoading by viewModel.loadingReorderItems.collectAsState()

    val list = currentTrip?.daysWithPoints ?: listOf()

    if (list.isEmpty() || dayActive.value.dayId == null || list.size <= dayActive.value.dayIndex || list[dayActive.value.dayIndex].day.dayId != dayActive.value.dayId) {
        if (list.isEmpty()) {
            viewModel.changeDayActive(null, 0)
        } else {
            viewModel.changeDayActive(list[0].day.dayId, 0)
        }
    }
    val position by animateFloatAsState(if (scrollUpState.value == true) -400f else 0f)
    AppTheme() {
        val configuration = LocalConfiguration.current

        Surface(
            modifier = Modifier.graphicsLayer { translationY = (position) },
        ) {
            Scaffold(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .height(200.dp),
                topBar = {
                    SmallTopAppBar(
                        modifier = Modifier.zIndex(1F).testTag("trip_screen_tag"),
                        navigationIcon = {
                            IconButton(onClick = { viewModel.upPress() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },
                        title = {
                                Text(
                                    text = "",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )

                        },actions = {
                            if(!checkForInternet(LocalContext.current.applicationContext)) {
                                Icon(
                                    modifier = Modifier.height(24.dp).width(24.dp),
                                    painter = painterResource(id = R.drawable.internet),
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = stringResource(R.string.no_internet)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    )
                },
            ) {
                currentTrip?.trip?.let { it1 ->
                    if (it1.defaultPhoto.isEmpty()) {
                        Surface(
                            modifier = Modifier
                                .zIndex(3F)
                                .offset((configuration.screenWidthDp.dp) / 2 - 45.dp, (-55).dp),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Image(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .height(90.dp)
                                    .width(90.dp),
                                painter = painterResource(id = R.drawable.road_sign),
                                contentDescription = null
                            )
                        }
                    } else {
                        TripImage(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .zIndex(3F)
                                .offset((configuration.screenWidthDp.dp) / 2 - 45.dp, (-55).dp),
                            imageUrl = it1.defaultPhoto,
                            contentDescription = null
                        )
                    }

                }

                if (list.isNotEmpty() && list.size > dayActive.value.dayIndex) {
                    val activeDay = list[dayActive.value.dayIndex]
                    if (activeDay.day.codeWeather > 0) {
                        Row(
                            Modifier
                                .zIndex(10F)
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Image(
                                painter = ChoosePainter(activeDay.day.codeWeather),
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 12.dp)
                                    .offset((-16).dp, 0.dp),
                                text = "${activeDay.day.minTemperature.toInt()}/${activeDay.day.maxTemperature.toInt()}°C ${activeDay.day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("en"))} ${activeDay.day.date.dayOfMonth} ${DateFormatSymbols().shortMonths[activeDay.day.date.monthValue - 1]}",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    } else {
                        Row(
                            Modifier
                                .zIndex(10F)
                                .padding(top = 55.dp, start = 30.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = 12.dp),
                                text = "${activeDay.day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("en"))} ${activeDay.day.date.dayOfMonth} ${DateFormatSymbols().shortMonths[activeDay.day.date.monthValue - 1]}",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }


                }

                Surface(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .offset(0.dp, -1.dp),
                    shape = MenuShape(),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .height(130.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = stringResource(R.string.info),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )
                            IconButton(modifier = Modifier.testTag("trip_screen_button_info_tag"), onClick = {
                                navigate(Routes.TripMainInfoRoute.createRoute(tripId))
                            }) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .zIndex(1F)
                        .offset(0.dp, 70.dp)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp, start = 0.dp, end = 0.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        items(list.size) { item ->
                            Box(modifier = Modifier.clickable {
                                if (item < list.size) {
                                    viewModel.changeDayActive(list[item].day.dayId, item)
                                }
                            }) {
                                var weekEnd = false
                                if (item < list.size) {
                                   if(list[item].day.date.dayOfWeek.ordinal == 6 || list[item].day.date.dayOfWeek.ordinal == 7){
                                       weekEnd = true
                                   }
                                }
                                CustomDayChip(
                                    selected = item == dayActive.value.dayIndex,
                                    text = "${item + 1} day",
                                    weekEnd = weekEnd
                                )
                            }

                        }
                    }
                    if (statusReorderLoading.status == LoadingState.Status.RUNNING) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                        ) {
                            LinearProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScrollableAppBarPreview() {
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
        val navController: NavController =
            NavController(context = LocalContext.current.applicationContext)
        AppTheme() {
            ScrollableAppBar(
                tripId = 0,
                navigate = {},
                viewModel = tripViewModel,
            )
        }


    }

}

@Composable
fun ChoosePainter(weatherCode: Int): Painter {
    return when (weatherCode) {
        1 -> painterResource(id = R.drawable.weather1)
        2 -> painterResource(id = R.drawable.weather2)
        4 -> painterResource(id = R.drawable.weather4)
        5 -> painterResource(id = R.drawable.weather5)
        6 -> painterResource(id = R.drawable.weahter6)
        9 -> painterResource(id = R.drawable.weather9)
        11 -> painterResource(id = R.drawable.weather11)
        12 -> painterResource(id = R.drawable.weather12)
        13 -> painterResource(id = R.drawable.weather15)
        14 -> painterResource(id = R.drawable.weather14)
        15 -> painterResource(id = R.drawable.weather15)
        16 -> painterResource(id = R.drawable.weather16)
        17 -> painterResource(id = R.drawable.weather17)
        18 -> painterResource(id = R.drawable.weather18)
        20 -> painterResource(id = R.drawable.weather20)
        22 -> painterResource(id = R.drawable.weather22)
        25 -> painterResource(id = R.drawable.weather25)
        28 -> painterResource(id = R.drawable.weather28)
        29 -> painterResource(id = R.drawable.weather29)
        30 -> painterResource(id = R.drawable.weather30)
        34 -> painterResource(id = R.drawable.weather34)
        35 -> painterResource(id = R.drawable.weather35)
        36 -> painterResource(id = R.drawable.weather36)
        37 -> painterResource(id = R.drawable.weather37)
        38 -> painterResource(id = R.drawable.weather38)
        39 -> painterResource(id = R.drawable.weather39)
        40 -> painterResource(id = R.drawable.weather40)
        41 -> painterResource(id = R.drawable.weather41)
        29 -> painterResource(id = R.drawable.weather29)
        else -> painterResource(id = R.drawable.weather_unknown)
    }
}

