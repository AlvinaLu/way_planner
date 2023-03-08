package cz.cvut.fel.lushnalv.ui.theme.mapScreen

import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.components.MapAppBar
import cz.cvut.fel.lushnalv.components.MapView
import cz.cvut.fel.lushnalv.components.SearchTextField
import cz.cvut.fel.lushnalv.data.measure
import cz.cvut.fel.lushnalv.models.CandidatePlace
import cz.cvut.fel.lushnalv.models.TypeOfDayPoint
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import kotlin.reflect.KFunction3


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun MapContent(
    listCandidatePlaces: List<CandidatePlace>,
    upPress: () -> Unit,
    getAllPlaces: KFunction3<RequestGooglePlace, Int, LatLng, Unit>,
    loadingState: LoadingState,
    searchState: SearchState,
    changeSearchQuery: (String) -> Unit,
    getPLacesBySearch: (Int, LatLng) -> Unit,
    changeTypeToNotLoaded: () -> Unit,
    searchQueryClear: () -> Unit,
    changeSearchIconState: (TypeOfDayPoint) -> Unit,
    searchIconState: TypeOfDayPoint,
    getCustomPlace: (TypeOfDayPoint) -> Unit,
    permissionsState: MultiplePermissionsState,
    getCandidatePlaceDetails: (String, TypeOfDayPoint) -> Unit,
    getCandidateUnknownSource: (TypeOfDayPoint) -> Unit,
    dayTitle: String,
    viewModel: MapScreenModelView
) {
    var isMapLoaded by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(50.1112547, 14.4985546), 13.4f)
    }



    val localFocusManager = LocalFocusManager.current

    Scaffold() {
        MapAppBar(title = dayTitle, navigate = upPress)
        Column(
            modifier = Modifier
                .padding(top = 140.dp)
                .fillMaxHeight()
        ) {
            if (loadingState.status == LoadingState.Status.FAILED) {
                Toast.makeText(
                    LocalContext.current,
                    loadingState.msg,
                    Toast.LENGTH_LONG
                ).show()
                changeTypeToNotLoaded()
            }
            SearchTextField(
                name = searchState.searchQuery,
                onValueChange = changeSearchQuery,
                isValid = searchState.validSearch,
                getPLacesBySearch,
                searchQueryClear,
                localFocusManager, viewModel, cameraPositionState
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val (focusRequester) = FocusRequester.createRefs()
                IconButton(
                    onClick = {
                        getAllPlaces(
                            RequestGooglePlace.restaurant,
                            measure(cameraPositionState),
                            cameraPositionState.position.target
                        )
                        localFocusManager.moveFocus(FocusDirection.Down)
                        changeSearchIconState(TypeOfDayPoint.FOOD)
                    },
                    modifier = Modifier.focusable()
                ) {
                    Icon(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        tint = MaterialTheme.colorScheme.primary,
                        painter = painterResource(R.drawable.cafe_outline),
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    getAllPlaces(
                        RequestGooglePlace.tourist_attraction,
                        measure(cameraPositionState),
                        cameraPositionState.position.target
                    )
                    localFocusManager.moveFocus(FocusDirection.Down)
                    changeSearchIconState(TypeOfDayPoint.SIGHTS)
                }, modifier = Modifier.focusable()) {
                    Icon(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        tint = MaterialTheme.colorScheme.primary,
                        painter = painterResource(R.drawable.camera_outline),
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    getAllPlaces(
                        RequestGooglePlace.hotel,
                        measure(cameraPositionState),
                        cameraPositionState.position.target
                    )
                    localFocusManager.moveFocus(FocusDirection.Down)
                    changeSearchIconState(TypeOfDayPoint.HOTEL)
                }, modifier = Modifier.focusable()) {
                    Icon(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        tint = MaterialTheme.colorScheme.primary,
                        painter = painterResource(R.drawable.hotel_outline),
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    getAllPlaces(
                        RequestGooglePlace.gas_station,
                        measure(cameraPositionState),
                        cameraPositionState.position.target
                    )
                    localFocusManager.moveFocus(FocusDirection.Down)
                    changeSearchIconState(TypeOfDayPoint.GAS)
                }, modifier = Modifier.focusable()) {
                    Icon(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        tint = MaterialTheme.colorScheme.primary,
                        painter = painterResource(R.drawable.local_gas_station__outline),
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        changeSearchIconState(TypeOfDayPoint.UNKNOWN)
                        localFocusManager.moveFocus(FocusDirection.Down)
                        getCustomPlace(TypeOfDayPoint.UNKNOWN)
                    }, modifier = Modifier.focusable().testTag("map_screen_unknown_icon_tag")
                ) {
                    Icon(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        tint = MaterialTheme.colorScheme.primary,
                        painter = painterResource(R.drawable.place__outline),
                        contentDescription = null
                    )
                }
            }
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val (
                    linearProgressIndicator, map,
                ) = createRefs()
                Box(modifier = Modifier
                    .padding(bottom = 0.dp)
                    .constrainAs(map) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }) {
                    MapView(listCandidatePlaces, cameraPositionState, onMapLoaded = {
                        isMapLoaded = true
                    }, searchIconState, permissionsState, getCandidatePlaceDetails, getCandidateUnknownSource, viewModel)
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
                if (loadingState.status == LoadingState.Status.RUNNING) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp)
                            .constrainAs(linearProgressIndicator) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                            }, color = MaterialTheme.colorScheme.primary
                    )
                }
            }


        }

    }
}


