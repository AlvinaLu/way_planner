package cz.cvut.fel.lushnalv

import TripCard
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import cz.cvut.fel.lushnalv.models.Trip
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState

@Composable
fun MainContent(
    navigate: (String) -> Unit,
    planned: Boolean,
    allTrips: List<Trip>,
    statusRefresh: Boolean,
    fetchAllTrips: () -> Unit,
    statusLoading: LoadingState,
    changeStatus: (LoadingState) -> Unit,
    appState: WayPlannerAppState
) {

    AppTheme() {
        if (statusLoading.status == LoadingState.Status.RUNNING) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }else {
            if (statusLoading.status == LoadingState.Status.FAILED) {
                changeStatus(LoadingState.IDLE)
                Toast.makeText(
                    LocalContext.current,
                    statusLoading.msg ?: stringResource(R.string.error),
                    Toast.LENGTH_LONG
                ).show()
            }
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = statusRefresh),
                onRefresh = { fetchAllTrips() },
                indicator = { state, trigger ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = trigger,
                        scale = true,
                        backgroundColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                    )
                }
            ) {
                if (allTrips.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(count = allTrips.size) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp)
                            ) {
                                TripCard(trip = allTrips[it], navigate = navigate, appState = appState)
                            }

                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, bottom = 130.dp)
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val text = if (planned) {
                            stringResource(R.string.you_havent_planned_trips_yet)
                        } else {
                            stringResource(R.string.you_havent_past_trips_yet)
                        }
                        Text(
                            text = text,
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }

    }
}

@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainContentPreview() {
    AppTheme {
        val navController: NavController =
            NavController(context = LocalContext.current.applicationContext)
    }
}
