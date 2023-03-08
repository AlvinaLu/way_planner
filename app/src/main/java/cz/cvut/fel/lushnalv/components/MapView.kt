package cz.cvut.fel.lushnalv.components

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.*
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.models.CandidatePlace
import cz.cvut.fel.lushnalv.models.TypeOfDayPoint
import cz.cvut.fel.lushnalv.ui.theme.createNewTrip.CreateTripViewModel
import cz.cvut.fel.lushnalv.ui.theme.mapScreen.MapScreenModelView
import kotlinx.coroutines.flow.MutableStateFlow


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapView(
    listEat: List<CandidatePlace>,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    onMapLoaded: () -> Unit = {},
    searchIconState: TypeOfDayPoint,
    permissionsState: MultiplePermissionsState,
    getCandidatePlaceDetails: (placeId: String, type: TypeOfDayPoint) -> Unit,
    getCandidateUnknownSource: (TypeOfDayPoint) -> Unit,
    viewModel: MapScreenModelView
) {


    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.e("DEBUG", "${it.key} = ${it.value}")
        }
    }

    var properties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = false)) }


    Column() {

        PermissionsRequired(
            multiplePermissionsState = permissionsState,
            permissionsNotGrantedContent = {
                SideEffect {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            },
            permissionsNotAvailableContent = {}) {
            properties = MapProperties(isMyLocationEnabled = true)

        }

        if (cameraPositionState.isMoving) {
            viewModel.changeCameraPos(cameraPositionState.position)
        }

        GoogleMap(
            onMapLoaded = onMapLoaded,
            modifier = Modifier.fillMaxSize().testTag("map_screen_map_tag"),
            cameraPositionState = cameraPositionState,
            properties = properties
        ) {

            if (searchIconState == TypeOfDayPoint.UNKNOWN) {
                MapMarker(
                    context = LocalContext.current,
                    cityLocation = cameraPositionState.position.target,
                    iconResourceId = R.drawable.location_unknown,
                    title = "Tap to add to plan",
                    snippet = null,
                    onInfoWindowClick = { getCandidateUnknownSource(TypeOfDayPoint.UNKNOWN) }
                )
            } else {
                listEat.forEachIndexed { index, it ->
                    var icon = when (it.type) {
                        TypeOfDayPoint.FOOD -> R.drawable.location_cup
                        TypeOfDayPoint.HOTEL -> R.drawable.location_hotel
                        TypeOfDayPoint.SIGHTS -> R.drawable.location_photo
                        TypeOfDayPoint.GAS -> R.drawable.location_gas
                        TypeOfDayPoint.CUSTOM -> R.drawable.location_custom
                        else -> R.drawable.location_unknown
                    }

                    val markerClick: (Marker) -> Unit = { marker ->
                        Log.d(TAG, "${marker.title} was clicked")
                        cameraPositionState.projection?.let { projection ->
                            Log.d(TAG, "The current projection is: $projection")
                        }
                        getCandidatePlaceDetails(it.googleId, it.type)
                    }

                    MapMarker(
                        context = LocalContext.current,
                        cityLocation = LatLng(it.lat, it.lng),
                        iconResourceId = icon,
                        title = stringResource(R.string.tap_to_add_to_paln),
                        snippet = it.name,
                        onInfoWindowClick = markerClick
                    )
                }
            }


        }

    }
}


@Composable
fun MapMarker(
    context: Context,
    cityLocation: LatLng,
    @DrawableRes iconResourceId: Int,
    title: String,
    snippet: String?,
    onInfoWindowClick: (Marker) -> Unit = {},
) {
    val icon = bitmapDescriptorFromVector(
        context, iconResourceId
    )
    Marker(
        state = MarkerState(position = cityLocation),
        icon = icon,
        zIndex = 0.5F,
        title = title,
        snippet = snippet,
        onInfoWindowClick = onInfoWindowClick
    )
}

@Composable
fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {
    val locationIconBackground =
        ContextCompat.getDrawable(context, R.drawable.location_background) ?: return null
    locationIconBackground.setBounds(
        0,
        0,
        locationIconBackground.intrinsicWidth,
        locationIconBackground.intrinsicHeight
    )
    locationIconBackground.setTint(
        argb(
            MaterialTheme.colorScheme.primary.alpha,
            MaterialTheme.colorScheme.primary.red,
            MaterialTheme.colorScheme.primary.green,
            MaterialTheme.colorScheme.primary.blue
        )
    )

    val bm = Bitmap.createBitmap(
        locationIconBackground.intrinsicWidth,
        locationIconBackground.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    val locationIconForeground = ContextCompat.getDrawable(context, vectorResId) ?: return null
    locationIconForeground.setBounds(
        0,
        0,
        locationIconForeground.intrinsicWidth,
        locationIconForeground.intrinsicHeight
    )
    locationIconForeground.setTint(
        argb(
            MaterialTheme.colorScheme.background.alpha,
            MaterialTheme.colorScheme.background.red,
            MaterialTheme.colorScheme.background.green,
            MaterialTheme.colorScheme.background.blue
        )
    )


    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)

    locationIconBackground.draw(canvas)
    locationIconForeground.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapViewForCreateTrip(
    onMapLoaded: () -> Unit = {},
    searchIconState: TypeOfDayPoint = TypeOfDayPoint.UNKNOWN,
    permissionsState: MultiplePermissionsState,
    getCandidateUnknownSource: (TypeOfDayPoint) -> Unit,
    viewModel: CreateTripViewModel
) {

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.e("DEBUG", "${it.key} = ${it.value}")
        }
    }

    var properties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = false)) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(50.1112547, 14.4985546), 13.4f)
    }


    Column() {

        PermissionsRequired(
            multiplePermissionsState = permissionsState,
            permissionsNotGrantedContent = {
                SideEffect {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            },
            permissionsNotAvailableContent = {}) {
            properties = MapProperties(isMyLocationEnabled = true)

        }

        if (cameraPositionState.isMoving) {
           viewModel.changeCameraPos(cameraPositionState.position)
        }

        GoogleMap(
            onMapLoaded = onMapLoaded,
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties
        ) {
            MapMarker(
                context = LocalContext.current,
                cityLocation = cameraPositionState.position.target,
                iconResourceId = R.drawable.location_unknown,
                title = "Start point",
                snippet = null,
            )
        }

    }


}



