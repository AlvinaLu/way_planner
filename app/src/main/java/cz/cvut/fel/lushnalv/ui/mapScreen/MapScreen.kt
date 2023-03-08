package cz.cvut.fel.lushnalv.ui.theme.mapScreen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chargemap.compose.numberpicker.Hours
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.models.CandidatePlace
import cz.cvut.fel.lushnalv.models.TypeOfDayPoint
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.BottomSheetShape
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import java.time.Duration

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,
    ExperimentalPermissionsApi::class,
)
@Composable
fun MapScreen(
    viewModel: MapScreenModelView,
    appState: WayPlannerAppState,
    permissionsState: MultiplePermissionsState,
    dayId: Long,
    dayTitle: String
) {
    val allCandidatePlaces by viewModel.allCandidatePlaces.observeAsState(initial = listOf())
    val loadingState: LoadingState by viewModel.loadingState.collectAsState()
    val searchState: SearchState by viewModel.searchState.collectAsState()
    val searchIconState: TypeOfDayPoint by viewModel.searchIconState.collectAsState()
    val candidatePlace: CandidatePlace by viewModel.candidatePlacesById.observeAsState(initial = CandidatePlace())

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )

    val coroutineScope = rememberCoroutineScope()

    fun getCandidatePlaceDetails(placeId: String, type: TypeOfDayPoint){
        viewModel.getInfoDetails(placeId, type, bottomSheetScaffoldState, coroutineScope)
    }

    fun getCandidateUnknownSource(type: TypeOfDayPoint){
        viewModel.openUnknownSourceEditWindow(bottomSheetScaffoldState, coroutineScope)
    }

    fun createDayPoint(hours: Hours, photoIndex: Int){
        val duration = Duration.ofHours(hours.hours.toLong()).plus(Duration.ofMinutes(hours.minutes.toLong()))
        viewModel.addDayPoint(dayId = dayId, duration = duration, photoIndex = photoIndex)
    }



    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("map_screen_tag"),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            BottomSheetScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                scaffoldState = bottomSheetScaffoldState,
                sheetShape = BottomSheetShape,
                drawerElevation = 5.dp,
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    ) {
                        if(searchIconState==TypeOfDayPoint.UNKNOWN){
                            MapPointInformationUnknownSource(viewModel, dayId = dayId)
                        }else {
                            MapPointInformation(candidatePlace, ::createDayPoint, viewModel::closeBottomScaffold, viewModel = viewModel, 0, scaffoldState = bottomSheetScaffoldState)
                        }
                    }
                }, sheetPeekHeight = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    MapContent(
                        allCandidatePlaces,
                        viewModel::upPress,
                        viewModel::getAllPLaces,
                        loadingState,
                        searchState,
                        viewModel::searchQueryChange,
                        viewModel::getPLacesBySearch,
                        viewModel::changeTypeToNotLoaded,
                        viewModel::searchQueryClear,
                        viewModel::changeSearchIconState,
                        searchIconState,
                        viewModel::getCustomPlace,
                        permissionsState,
                        ::getCandidatePlaceDetails,
                        ::getCandidateUnknownSource,
                        dayTitle,
                        viewModel
                    )
                }

            }

        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MapScreenPreview() {
    AppTheme {

    }
}
