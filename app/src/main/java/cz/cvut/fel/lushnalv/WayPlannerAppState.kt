package cz.cvut.fel.lushnalv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun rememberWayPlannerAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) =
    remember(navController,  coroutineScope) {
        WayPlannerAppState( navController,  coroutineScope)
    }
@Stable
class WayPlannerAppState(val navController: NavHostController, coroutineScope: CoroutineScope) {

   val stateTripId = MutableStateFlow(0L)

    fun navigateToTrip(tripId: Long){
        navController.navigate(Routes.TripRoute.createRoute(tripId)){
            launchSingleTop = true
            popUpTo(Routes.MainRoute.route) {
                inclusive = true
            }
        }

    }

    fun deleteTripNavigateToMain() {
        navController.navigate(Routes.MainRoute.route){
            popUpTo(navController.graph.findStartDestination().id){
                inclusive = true
            }
        }
    }

    fun upPress() {
        navController.navigateUp()
    }

    fun navigateFromAuthToMain(){
        navController.navigate(Routes.MainRoute.route){
            launchSingleTop = true
            popUpTo(Routes.AuthRoute.route) {
                inclusive = true
            }
        }
    }

    fun navigateToAuth(){
        navController.navigate(Routes.AuthRoute.route){
            launchSingleTop = true
            popUpTo(Routes.MainRoute.route) {
                inclusive = true
            }
        }
    }

    fun navigateTo(route: String){
        navController.navigate(route){
            launchSingleTop = true

        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}