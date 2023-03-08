package cz.cvut.fel.lushnalv.ui.tripScreen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripViewModel

@Composable
fun TripContent(viewModel: TripViewModel, appState: WayPlannerAppState, tripId: Long) {

    val statusLoading by viewModel.loadingState.collectAsState()
    val currentTrip by viewModel.tripById.observeAsState()

    val dayActive = viewModel.dayActive.collectAsState()

    if (statusLoading.status == LoadingState.Status.RUNNING) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        if (statusLoading.status == LoadingState.Status.FAILED) {
            viewModel.changeStatus(LoadingState.IDLE)
            Toast.makeText(
                LocalContext.current,
                statusLoading.msg ?: stringResource(R.string.error),
                Toast.LENGTH_LONG
            ).show()
        }
        if (currentTrip == null || currentTrip!!.daysWithPoints.isEmpty() || currentTrip!!.daysWithPoints.size > dayActive.value.dayIndex && currentTrip!!.daysWithPoints[dayActive.value.dayIndex].points.isEmpty()) {
            Note()
        } else {
            val dayPointsList =
                currentTrip!!.daysWithPoints[dayActive.value.dayIndex].points.sortedBy { it.date }.toMutableStateList()
            val day = if(dayActive.value.dayIndex < currentTrip!!.daysWithPoints.size){ currentTrip!!.daysWithPoints[dayActive.value.dayIndex]} else null
            DayPointsList(
                day,
                dayPointsList,
                appState,
                tripId,
                { fromIndex, toIndex ->
                    run {
                        dayPointsList.move(
                            fromIndex,
                            toIndex
                        )
                        viewModel.reorderItems(
                            fromId = dayPointsList[toIndex].dayPointId,
                            toId = dayPointsList[fromIndex].dayPointId
                        )
                    }
                },
                viewModel
            )
        }
    }

}

@Composable
fun Note() {
    AppTheme() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.havent_planned_activities),
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }

}
