package cz.cvut.fel.lushnalv.ui.daypoint

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberPermissionState
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.components.AddExpenseDialog
import cz.cvut.fel.lushnalv.components.ImageChosen
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.BottomSheetShape
import cz.cvut.fel.lushnalv.ui.theme.DragHandleShape
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import cz.cvut.fel.lushnalv.utils.checkForInternet
import cz.cvut.fel.lushnalv.utils.cut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DayPointScreen(
    viewModel: DayPointViewModel,
    navController: NavController,
    tripId: Long,
    dayPointId: Long,
    permissionsStateCamera: MultiplePermissionsState,
    permissionsStateDocuments: MultiplePermissionsState
) {
    viewModel.putDayPointId(dayPointId)
    viewModel.putTripId(tripId)
    viewModel.getDayPointWeb(dayPointId = dayPointId)

    DayPointContent(
        viewModel = viewModel,
        dayPointId = dayPointId,
        navController,
        permissionsStateCamera
    )


}


@OptIn(
    ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)
@Composable
fun DayPointContent(
    viewModel: DayPointViewModel,
    dayPointId: Long,
    navController: NavController,
    permissionsStateCamera: MultiplePermissionsState,
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    val coroutineScope = rememberCoroutineScope()

    val albums by viewModel.albums.observeAsState(initial = mutableListOf())
    val dayPoint by viewModel.dayPointById.observeAsState()
    val currentTrip by viewModel.tripById.observeAsState()
    val userList by viewModel.userListByDayPointId.observeAsState()
    val dayPointChipState: DayPointChips by viewModel.dayPointChipState.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val buttonAddPhotoIsClicked = remember { mutableStateOf(false) }
    val buttonAddFileIsClicked = remember { mutableStateOf(false) }

    fun clickedButtonAddPhoto() {
        buttonAddPhotoIsClicked.value = true
    }


    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            println("selected file URI ${it.data?.data}")
            if (it.data?.data != null) {
                val uri = it.data!!.data!!
                viewModel.uploadFile(dayPointId, it.data!!.data!!)
            } else {
            }
            buttonAddFileIsClicked.value = false
        }

    val intent = Intent(
        Intent.ACTION_OPEN_DOCUMENT,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    ).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/pdf"
    }


    val permissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val lifecycleOwner = LocalLifecycleOwner.current


    if (buttonAddFileIsClicked.value) {
        DisposableEffect(key1 = lifecycleOwner, effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        permissionState.launchPermissionRequest()
                    }
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        })
        when {
            permissionState.hasPermission -> {
                launcher.launch(intent)
            }
            permissionState.shouldShowRationale -> {
                Column {
                    Text(text = "Reading external permission is required by this app")
                }
            }
            !permissionState.hasPermission && !permissionState.shouldShowRationale -> {
                Text(text = "Permission fully denied. Go to settings to enable")
            }
        }
    }

    if (showDialog.value)
        currentTrip?.let { trip ->
            AddExpenseDialog(setShowDialog = {
                showDialog.value = it
            }, viewModel = viewModel, trip = trip, dayPointId = dayPointId)
        }



    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent).testTag("day_point_screen_tag"),
        topBar = {
            if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                SmallTopAppBar(
                    title = {
                        DropDownMenuInTopAppBar(viewModel)
                    },
                    navigationIcon = {
                        IconButton(onClick = {   coroutineScope.launch {
                            if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                bottomSheetScaffoldState.bottomSheetState.collapse()
                            }
                        }}) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.uploadImages(dayPointId = dayPointId)
                                coroutineScope.launch {
                                    if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint =  MaterialTheme.colorScheme.background,
                                modifier = Modifier.clip(
                                    CircleShape
                                ).background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                )
            } else {
                SmallTopAppBar(
                    title = {
                        if (dayPoint?.dayPoint?.title != null) {
                            Text(
                                text = "${dayPoint?.dayPoint?.title}".cut(20),
                                maxLines = 1,
                            )
                        } else {
                            Text("")
                        }
                    },
                    navigationIcon = {
                        IconButton(modifier = Modifier.testTag("ArrowBack"), onClick = { viewModel.upPress() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        if(!checkForInternet(LocalContext.current.applicationContext)) {
                            Icon(
                                modifier = Modifier.height(24.dp).width(24.dp),
                                painter = painterResource(id = R.drawable.internet),
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "No internet connection"
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
            }
        },
        floatingActionButton = {
            if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                when (dayPointChipState.ordinal) {
                    0 -> ExtendedFloatingActionButton(
                        modifier = Modifier.testTag("Add expense"),
                        text = { Text(stringResource(R.string.add_expence)) },
                        onClick = {
                            showDialog.value = true
                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )

                    1 -> ExtendedFloatingActionButton(
                        text = { Text("Add file") },
                        onClick = {
                            buttonAddFileIsClicked.value = true
                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    else -> {}
                }

            }
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.Transparent)
        ) {
            BottomSheetScaffold(
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.75F)
                            .background(Color.Transparent)
                    ) {
                        BottomSheetContent(
                            viewModel = viewModel,
                            dayPointId = dayPointId,
                            navController = navController,
                            permissionsStateCamera, albums,
                            buttonAddPhotoIsClicked.value,
                            bottomSheetScaffoldState = bottomSheetScaffoldState,
                            coroutineScope = coroutineScope,
                        )
                    }
                },
                scaffoldState = bottomSheetScaffoldState,
                sheetShape = BottomSheetShape,
                drawerElevation = 5.dp,
                sheetPeekHeight = 0.dp
            ) {
                DayPointContainer(
                    viewModel = viewModel,
                    dayPointId = dayPointId,
                    navController = navController,
                    permissionsStateCamera = permissionsStateCamera,
                    bottomSheetScaffoldState = bottomSheetScaffoldState,
                    coroutineScope = coroutineScope,
                    dutyListWithUsers = userList,
                    clickedButtonAddPhoto = ::clickedButtonAddPhoto
                )
            }

        }

    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropDownMenuInTopAppBar(
    viewModel: DayPointViewModel,
) {
    val albums by viewModel.albums.observeAsState(initial = mutableListOf())
    var expanded by remember { mutableStateOf(false) }
    val selectedIndex by viewModel.optionMenuChosen.collectAsState()

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (albums.isNotEmpty()) {
                Text(
                    text = albums[selectedIndex].name,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                )
                Icon(
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                    tint = MaterialTheme.colorScheme.onBackground,
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                )
            }
        }

        ExposedDropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            if (albums.isNotEmpty()) {
                albums.forEachIndexed { index, selectionOption ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),
                        content = {
                            Text(
                                text = selectionOption.name,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        onClick = {
                            viewModel.putOptionMenuChosen(index)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(
    viewModel: DayPointViewModel,
    dayPointId: Long, navController: NavController,
    permissionsStateCamera: MultiplePermissionsState,
    albums: List<Album>,
    buttonAddPhotoIsClicked: Boolean,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
) {

    val scrollState = rememberLazyGridState()
    val selectedIndex by viewModel.optionMenuChosen.collectAsState()
    val statusLoadingPhoto: LoadingState by viewModel.loadingStatePhoto.collectAsState()
    val coroutineScope = rememberCoroutineScope()


    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.e("DEBUG", "${it.key} = ${it.value}")
        }
    }

    if (statusLoadingPhoto.status == LoadingState.Status.RUNNING) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        if (statusLoadingPhoto.status == LoadingState.Status.FAILED) {
            viewModel.changeStatusPhoto(LoadingState.IDLE)
            Toast.makeText(
                LocalContext.current,
                statusLoadingPhoto.msg ?: stringResource(R.string.error),
                Toast.LENGTH_LONG
            ).show()
        }
        if (buttonAddPhotoIsClicked) {
            PermissionsRequired(
                multiplePermissionsState = permissionsStateCamera,
                permissionsNotGrantedContent = {
                    SideEffect {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        )
                    }
                },
                permissionsNotAvailableContent = {}) {

                viewModel.getAllImages()

                AppTheme() {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        shape = BottomSheetShape,
                        color = MaterialTheme.colorScheme.background

                    ) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            Surface(
                                modifier = Modifier
                                    .absolutePadding(16.dp, 16.dp, 16.dp, 0.dp)
                                    .width(50.dp)
                                    .height(6.dp)
                                    .shadow(2.dp),
                                shape = DragHandleShape,
                                color = MaterialTheme.colorScheme.primary,
                            ) {

                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (albums.isNotEmpty() && albums.size > selectedIndex && albums[selectedIndex].images.isNotEmpty()) {
                                        LazyVerticalGrid(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.background)
                                                .padding(start = 2.dp, end = 2.dp),
                                            columns = GridCells.Fixed(3),
                                            state = scrollState
                                        ) {
                                            items(count = albums[selectedIndex].images.size) {
                                                var roundedCornerShape =
                                                    RoundedCornerShape(0.dp)
                                                if (it == 0) {
                                                    roundedCornerShape =
                                                        RoundedCornerShape(topStart = 10.dp)
                                                }
                                                if (it == 2) {
                                                    roundedCornerShape =
                                                        RoundedCornerShape(topEnd = 10.dp)
                                                }
                                                if (it == albums[selectedIndex].images.size - 3) {
                                                    roundedCornerShape =
                                                        RoundedCornerShape(bottomStart = 10.dp)
                                                }
                                                if (it == albums[selectedIndex].images.size - 1) {
                                                    roundedCornerShape =
                                                        RoundedCornerShape(bottomEnd = 10.dp)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .background(MaterialTheme.colorScheme.background)
                                                        .padding(2.dp)
                                                        .clip(roundedCornerShape)
                                                ) {

                                                    ImageChosen(
                                                        image = albums[selectedIndex].images[it],
                                                        roundedCornerShape = roundedCornerShape,
                                                        viewModel = viewModel
                                                    )

                                                }

                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


