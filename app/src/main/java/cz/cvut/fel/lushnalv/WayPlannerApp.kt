package cz.cvut.fel.lushnalv

import android.Manifest
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.tripScreen.TripInfoScreen
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointScreen
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointViewModel
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointViewModelFactory
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.authorization.AuthScreen
import cz.cvut.fel.lushnalv.ui.theme.authorization.AuthViewModel
import cz.cvut.fel.lushnalv.ui.theme.authorization.AuthViewModelFactory
import cz.cvut.fel.lushnalv.ui.theme.createNewTrip.CreateTripScreen
import cz.cvut.fel.lushnalv.ui.theme.createNewTrip.CreateTripViewModel
import cz.cvut.fel.lushnalv.ui.theme.createNewTrip.CreateTripViewModelFactory
import cz.cvut.fel.lushnalv.ui.theme.main.MainScreen
import cz.cvut.fel.lushnalv.ui.theme.main.MainViewModel
import cz.cvut.fel.lushnalv.ui.theme.main.MainViewModelFactory
import cz.cvut.fel.lushnalv.ui.theme.mapScreen.MapScreen
import cz.cvut.fel.lushnalv.ui.theme.mapScreen.MapScreenModelView
import cz.cvut.fel.lushnalv.ui.theme.mapScreen.MapScreenModelViewFactory
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripScreen
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripViewModel
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripViewModelFactory
import cz.cvut.fel.lushnalv.ui.tripInfoScreen.TripInfoViewModel
import cz.cvut.fel.lushnalv.ui.tripInfoScreen.TripMainViewModelFactory

sealed class Routes(val route: String) {
    object MainRoute : Routes("main")
    object AuthRoute : Routes("auth")
    object CreateNewTripRoute : Routes("main/new")
    object TripRoute : Routes("trip/{tripId}"){
        fun createRoute(tripId: Long) = "trip/${tripId}"
    }
    object TripMainInfoRoute : Routes("trip/{tripId}/info"){
        fun createRoute(tripId: Long) = "trip/${tripId}/info"
    }
    object TripMapRoute : Routes("trip/map/{dayId}/{dayTitle}"){
        fun createRoute(dayId: Long, dayTitle: String) = "trip/map/${dayId}/${dayTitle}"
    }
    object DayPointRoute : Routes("trip/{tripId}/{dayPointId}"){
        fun createRoute(tripId: Long, dayPointId: Long) = "trip/${tripId}/${dayPointId}"
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WayPlannerApp() {
    val appPreferences = AppPreferences.create(LocalContext.current.applicationContext)
    AppTheme {
        val appState = rememberWayPlannerAppState()
        val permissionsStateLocation = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        val permissionsStateCamera = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        val permissionsStateDocuments = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        val owner = LocalViewModelStoreOwner.current
        owner?.let {

            val mainViewModel: MainViewModel = viewModel(
                it,
                "MainViewModel",
                MainViewModelFactory(
                    LocalContext.current.applicationContext
                            as Application, appState
                )
            )
            val authViewModel: AuthViewModel = viewModel(
                it,
                "AuthViewModel",
                AuthViewModelFactory(
                    LocalContext.current.applicationContext
                            as Application, appState
                )
            )
            val createTripViewModel: CreateTripViewModel = viewModel(
                it,
                "CreateTripViewModel",
                CreateTripViewModelFactory(
                    LocalContext.current.applicationContext
                            as Application, appState
                )
            )
            val tripViewModel: TripViewModel = viewModel(
                it,
                "TripViewModel",
                TripViewModelFactory(
                    LocalContext.current.applicationContext
                            as Application, appState
                )
            )

            val mapScreenModelView: MapScreenModelView = viewModel(
                it,
                "MapScreenModelView",
                MapScreenModelViewFactory(
                    LocalContext.current.applicationContext
                            as Application, appState
                )
            )

            val tripMainInfoModelView: TripInfoViewModel = viewModel(
                it,
                "TripMainViewModel",
                TripMainViewModelFactory(
                    LocalContext.current.applicationContext
                            as Application, appState
                )
            )

            val dayPointViewModel: DayPointViewModel = viewModel(
                it,
                "DayPointViewModel",
                DayPointViewModelFactory(
                    LocalContext.current.applicationContext
                            as Application, appState
                )
            )

            val startDestination = if(appPreferences.accessToken == null) Routes.AuthRoute.route else Routes.MainRoute.route

            NavHost(navController = appState.navController, startDestination = startDestination) {
                composable(Routes.MainRoute.route) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary, modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        MainScreen(mainViewModel, appPreferences, appState
                        )
                    }

                }

                composable(Routes.AuthRoute.route) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary, modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        AuthScreen(authViewModel)
                    }

                }

                composable(Routes.CreateNewTripRoute.route) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary, modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        CreateTripScreen(createTripViewModel, permissionsStateLocation)
                    }

                }


                composable(
                    Routes.TripRoute.route,
                    arguments = listOf(navArgument("tripId") { type = NavType.LongType })
                ) { backStackEntry ->
                    backStackEntry.arguments?.getLong("tripId")?.let {
                        TripScreen(tripViewModel, appState, it)
                    }
                }

                composable(
                    Routes.TripMainInfoRoute.route,
                    arguments = listOf(navArgument("tripId") { type = NavType.LongType })
                ) { backStackEntry ->
                    backStackEntry.arguments?.getLong("tripId")?.let {
                       TripInfoScreen(tripMainInfoModelView, dayPointViewModel,  appState.navController, it)
                    }
                }
                composable(
                    Routes.TripMapRoute.route,
                    arguments = listOf(navArgument("dayId") { type = NavType.LongType },
                        navArgument("dayTitle") { type = NavType.StringType })
                ) { backStackEntry ->
                    backStackEntry.arguments?.getLong("dayId")?.let {dayId->

                        backStackEntry.arguments?.getString("dayTitle")?.let {dayTitle->
                            MapScreen(mapScreenModelView, appState, permissionsStateLocation, dayId, dayTitle)
                        }
                    }
                }

                composable(
                    Routes.DayPointRoute.route,
                    arguments = listOf(navArgument("tripId"){
                        type = NavType.LongType
                    }, navArgument("dayPointId") {
                        type = NavType.LongType })
                ) { backStackEntry ->
                    backStackEntry.arguments?.let {
                        val tripId = it.getLong("tripId")
                        val dayPointId = it.getLong("dayPointId")
                        DayPointScreen(viewModel = dayPointViewModel, navController = appState.navController, tripId = tripId, dayPointId = dayPointId, permissionsStateCamera, permissionsStateDocuments)
                    }
                }
            }
        }
    }

}

