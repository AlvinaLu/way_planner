package cz.cvut.fel.lushnalv.ui.theme.tripScreen

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.Routes
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.components.ScrollableAppBar
import cz.cvut.fel.lushnalv.models.*
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.tripScreen.TripContent
import java.text.DateFormatSymbols
import java.time.format.TextStyle
import java.util.*


@Composable
fun TripScreen(viewModel: TripViewModel, appState: WayPlannerAppState, tripId: Long) {

    viewModel.putTripId(tripId)
    viewModel.getTripFromWeb(tripId = tripId)

    AppTheme() {
        TripContainer(viewModel, tripId,   appState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripContainer(viewModel: TripViewModel, tripId: Long,   appState: WayPlannerAppState) {

    val dayActive = viewModel.dayActive.collectAsState()
    val currentTrip by viewModel.tripById.observeAsState()
    var dayTitle: String = ""

    if(dayActive.value!=null && !currentTrip?.daysWithPoints.isNullOrEmpty()){
        val activeDay = currentTrip!!.daysWithPoints.filter { it.day.dayId == dayActive.value.dayId }.firstOrNull()
        if(activeDay!=null){
            dayTitle = "${activeDay.day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("en"))} ${activeDay.day.date.dayOfMonth} ${DateFormatSymbols().shortMonths[activeDay.day.date.monthValue - 1]}"
        }
    }


    AppTheme() {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    modifier = Modifier.testTag("trip_screen_add_day_point_tag"),
                    text = { Text(text = stringResource(R.string.add_new)) },
                    onClick = {
                        dayActive.value.dayId?.let { Routes.TripMapRoute.createRoute(dayId = it, dayTitle = dayTitle) }
                            ?.let { viewModel.navigate(it) }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) }
                )
            })
        {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
            ) {

                TripContent(viewModel = viewModel, appState, tripId)


                ScrollableAppBar(
                    navigate = viewModel::navigate,
                    viewModel = viewModel, tripId = tripId
                )

            }
        }
    }
}


@Composable
fun TripImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    AppTheme() {
        Surface(
            modifier = modifier,
            shape = CircleShape,
            border = BorderStroke(0.dp, MaterialTheme.colorScheme.background)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                placeholder = painterResource(cz.cvut.fel.lushnalv.R.drawable.road_sign),
                error = painterResource(cz.cvut.fel.lushnalv.R.drawable.road_sign),
                modifier = Modifier
                    .height(90.dp)
                    .width(90.dp)
                    .shadow(1.dp),
                contentScale = ContentScale.FillHeight,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TripScreenPreview() {
    val owner = LocalViewModelStoreOwner.current

}