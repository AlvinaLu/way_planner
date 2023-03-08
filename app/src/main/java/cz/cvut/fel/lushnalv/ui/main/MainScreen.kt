package cz.cvut.fel.lushnalv.ui.theme.main

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cz.cvut.fel.lushnalv.MainContent
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.Routes
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.components.CustomRoundImageForChip
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.ui.main.NavigationDrawerItem
import cz.cvut.fel.lushnalv.ui.main.NavigationListItem
import cz.cvut.fel.lushnalv.ui.main.prepareNavigationDrawerItems
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.MainSheetShape
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import cz.cvut.fel.lushnalv.utils.checkForInternet
import cz.cvut.fel.lushnalv.utils.cut
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    appPreferences: AppPreferences,
    appState: WayPlannerAppState
) {

    viewModel.fetchAllTrips()
    MainContainer(viewModel = viewModel, appPreferences = appPreferences, appState = appState )

}
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainContainer(
    viewModel: MainViewModel,
    appPreferences: AppPreferences,
    appState: WayPlannerAppState
) {

    val allTrips by viewModel.allTrips.observeAsState(initial = listOf())
    val statusLoading: LoadingState by viewModel.stateStatus.collectAsState()
    val statusRefresh: Boolean by viewModel.stateRefresh.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    AppTheme() {
        androidx.compose.material.Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_screen"),
            scaffoldState = scaffoldState,
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(stringResource(R.string.way_planner))
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        IconButton(modifier = Modifier.testTag("menu"),
                            onClick = {
                            coroutineScope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = null
                            )
                        }
                    }, actions = {
                        if(!checkForInternet(LocalContext.current.applicationContext)) {
                            Icon(
                                modifier = Modifier.height(24.dp).width(24.dp),
                                painter = painterResource(id = R.drawable.internet),
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = stringResource(R.string.no_internet)
                            )
                        }
                    }
                )
            },
            drawerContent = {
                DrawerContent(
                    navigate = viewModel::navigate,
                    onClick = {
                        viewModel.logOut()
                        coroutineScope.launch {
                            delay(timeMillis = 250)
                            scaffoldState.drawerState.close()
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.testTag("create_trip_button_tag"),
                    onClick = {
                        viewModel.navigate(Routes.CreateNewTripRoute.route)
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                    )
                }
            }, content = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = MainSheetShape,
                        color = MaterialTheme.colorScheme.background,
                        shadowElevation = 3.dp,
                        tonalElevation = 3.dp
                    ) {
                        var selectedTabIndex by remember { mutableStateOf(0) }
                        val titles = listOf(stringResource(R.string.planned_trips), stringResource(R.string.past_trips))
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {

                                TabRow(
                                    selectedTabIndex = selectedTabIndex,
                                    divider = {},
                                    backgroundColor = MaterialTheme.colorScheme.background,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ) {
                                    titles.forEachIndexed { index, title ->
                                        val selected = selectedTabIndex == index
                                        Tab(
                                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                            text = {
                                                Text(
                                                    text = title,
                                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                                )
                                            },
                                            selected = selectedTabIndex == index,
                                            onClick = { selectedTabIndex = index },
                                            selectedContentColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            unselectedContentColor = MaterialTheme.colorScheme.errorContainer,
                                        )
                                    }
                                }
                                when (selectedTabIndex) {
                                    0 -> MainContent(
                                        navigate = viewModel::navigate,
                                        planned = true,
                                        allTrips = allTrips.filter { it.endDay > LocalDateTime.now() }.sortedByDescending{it.tripId},
                                        statusRefresh, viewModel::fetchAllTrips,
                                        statusLoading = statusLoading,
                                        viewModel::changeStatus,
                                        appState
                                    )
                                    1 -> MainContent(
                                        navigate = viewModel::navigate,
                                        planned = false,
                                        allTrips = allTrips.filter { it.endDay <= LocalDateTime.now() }.sortedByDescending{it.tripId},
                                        statusRefresh, viewModel::fetchAllTrips,
                                        statusLoading = statusLoading,
                                        viewModel::changeStatus,
                                        appState
                                    )
                                }


                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun DrawerContent(
    navigate: (String) -> Unit,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onPrimary
    ),
    onClick: () -> Unit
) {

    val appPreferences = AppPreferences.create(LocalContext.current.applicationContext)
    val itemsList = prepareNavigationDrawerItems(appPreferences, navigate)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors)),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 36.dp)
    ) {

        item {
            appPreferences.userDetails?.imgUrl?.let {
                CustomRoundImageForChip(
                    it,
                    null,
                    modifier = Modifier,
                    height = 150.dp,
                    width = 150.dp,
                    shadow = 3.dp,
                    shape = CircleShape
                )
            }

            if (appPreferences.userDetails !=null) {
                // user's name
                Text(
                    modifier = Modifier
                        .padding(top = 12.dp),
                    text = appPreferences.userDetails!!.name.cut(15),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    modifier = Modifier.padding(top = 8.dp, bottom = 30.dp),
                    text = appPreferences.userDetails!!.email,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
        item {
            NavigationListItem(
                appPreferences = appPreferences,
                item = NavigationDrawerItem(
                    image = Icons.Filled.ExitToApp,
                    label = "Logout",
                    showUnreadBubble = false,
                    route = Routes.AuthRoute.route
                ), itemClick = onClick
            )
        }
    }
}





@RequiresApi(Build.VERSION_CODES.O)
@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenPreview() {
    AppTheme {
        val navController: NavController =
            NavController(context = LocalContext.current.applicationContext)

    }
}



