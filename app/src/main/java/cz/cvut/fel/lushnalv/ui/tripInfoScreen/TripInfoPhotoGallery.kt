package cz.cvut.fel.lushnalv.ui.tripInfoScreen

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.components.CustomImageForTripMain
import cz.cvut.fel.lushnalv.components.MainImageInDayPointGallery
import cz.cvut.fel.lushnalv.ui.daypoint.LoadingDocumentState
import cz.cvut.fel.lushnalv.ui.theme.AppTheme

@Composable
fun TripInfoPhotoGallery(viewModel: TripInfoViewModel) {

    val currentTrip by viewModel.tripById.observeAsState()
    val context = LocalContext.current.applicationContext

    val listPhoto = mutableListOf<String>()
    val downloadStatePhoto by viewModel.downloadStatePhoto.collectAsState()

    currentTrip?.daysWithPoints?.forEach { dayWithPoints ->
        dayWithPoints?.points?.forEach { point ->
            point.getPhotoList().forEach { photo ->
                if (photo.isNotEmpty()) {
                    listPhoto.add(photo)
                }
            }
        }
    }
    if (listPhoto.isEmpty()) {
        currentTrip?.daysWithPoints?.forEach { dayWithPoints ->
            dayWithPoints?.points?.forEach { point ->
                if (point.defaultPhoto.isNotEmpty()) {
                    listPhoto.add(point.defaultPhoto)
                }
            }
        }
    }

    val index = remember { mutableStateOf(0) }
    val mainPhotoIndex = remember { mutableStateOf(0) }
    val visibleLeft = remember { mutableStateOf(false) }
    val visibleRight = remember { mutableStateOf(listPhoto.size > 1) }

    val launcherWrite = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (listPhoto.size > mainPhotoIndex.value) {
                viewModel.downloadImage(
                    listPhoto[mainPhotoIndex.value],
                    listPhoto[mainPhotoIndex.value].replaceBefore("%2F", "")
                        .replaceAfterLast("-firebase-id-", "").replace("%2F", "")
                        .replace("-firebase-id-", "")
                )
            }
            Log.d("DayPointPhotoGallery", "PERMISSION GRANTED")

        } else {
            // Permission Denied: Do something
            Log.d("DayPointPhotoGallery", "PERMISSION DENIED")
        }
    }


    fun openImage() {
        if (downloadStatePhoto.status == LoadingDocumentState.Status.SUCCESS) {
            viewModel.downloadStatePhoto(LoadingDocumentState.IDLE)
            Log.d("FileCard", "PERMISSION GRANTED")
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.setDataAndType(Uri.parse(downloadStatePhoto.msg), "image/jpg")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                ContextCompat.startActivity(context, intent, null)
            } catch (e: ActivityNotFoundException) {

            }
        }
    }

    val launcherRead = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openImage()
        } else {
            // Permission Denied: Do something
            Log.d("FileCard", "PERMISSION DENIED")
        }
    }

    if (listPhoto.size > 1) {
        visibleRight.value = true
    }

    fun goRight() {
        if (index.value + 4 < listPhoto.size) {
            index.value += 1
            mainPhotoIndex.value += 1
        } else if (index.value + 3 < listPhoto.size) {
            index.value += 1
            mainPhotoIndex.value += 1
        } else if (index.value + 2 < listPhoto.size) {
            if (mainPhotoIndex.value + 1 < listPhoto.size) {
                mainPhotoIndex.value += 1
            }
        } else if (index.value + 1 < listPhoto.size) {
            if (mainPhotoIndex.value + 1 < listPhoto.size) {
                mainPhotoIndex.value += 1
            }
        } else if (index.value < listPhoto.size) {
            if (mainPhotoIndex.value + 1 < listPhoto.size) {
                mainPhotoIndex.value += 1
            }
        }

        visibleRight.value = mainPhotoIndex.value + 1 < listPhoto.size
        visibleLeft.value = mainPhotoIndex.value > 0
    }

    fun goLeft() {
        if (mainPhotoIndex.value > 0) {
            if (index.value <= mainPhotoIndex.value && index.value > 0) {
                index.value -= 1
            }
            mainPhotoIndex.value -= 1
        }
        visibleRight.value = mainPhotoIndex.value + 1 < listPhoto.size
        visibleLeft.value = mainPhotoIndex.value > 0
    }
    if (downloadStatePhoto.status == LoadingDocumentState.Status.SUCCESS) {
        when (PackageManager.PERMISSION_GRANTED) {

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                openImage()
                Log.d(
                    "FileCard",
                    "Code requires permission"
                )
            }
            else -> {
                launcherRead.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    AppTheme() {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .height(250.dp)
        ) {
            ConstraintLayout() {
                val (
                    box, image, menu
                ) = createRefs()

                (if (listPhoto.isEmpty()) currentTrip?.trip?.defaultPhoto else listPhoto[mainPhotoIndex.value])?.let { it1 ->
                    MainImageInDayPointGallery(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .height(200.dp)
                            .drawWithCache {
                                val gradient = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Transparent
                                    ),
                                    startY = size.height / 3,
                                    endY = size.height
                                )
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(gradient, blendMode = BlendMode.Multiply)
                                }
                            },
                        imageUrl = it1,
                        contentDescription = null,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(box) {
                            start.linkTo(parent.start, 0.dp)
                            top.linkTo(parent.top, 0.dp)
                            bottom.linkTo(parent.bottom, 0.dp)
                            end.linkTo(parent.end, 0.dp)
                        }) {
                    Button(
                        onClick = { goLeft() },
                        enabled = visibleLeft.value,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(4.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.Black.copy(alpha = 0.3f),
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent
                        )
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                    Button(
                        onClick = { goRight() },
                        enabled = visibleRight.value,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(4.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.Black.copy(alpha = 0.3f),
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent
                        )
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
                if (listPhoto.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .constrainAs(menu) {
                                top.linkTo(parent.top, 0.dp)
                                end.linkTo(parent.end, 0.dp)
                            },
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Button(
                            onClick = {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ) -> {
                                        if (mainPhotoIndex.value < listPhoto.size) {
                                            viewModel.downloadImage(
                                                listPhoto[mainPhotoIndex.value],
                                                listPhoto[mainPhotoIndex.value].replaceBefore("%2F", "")
                                                    .replaceAfterLast("-firebase-id-", "").replace("%2F", "")
                                                    .replace("-firebase-id-", "")
                                            )
                                            Log.d(
                                                "ExampleScreen",
                                                "Code requires permission"
                                            )
                                        }
                                    }
                                    else -> {
                                        launcherWrite.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    }
                                }
                             },
                            modifier = Modifier
                                .size(50.dp),
                            shape = RoundedCornerShape(topEnd = 30.dp, bottomStart = 30.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.background,
                                containerColor = Color.Transparent
                            )
                        ) {
                            Icon(
                                painterResource(id = R.drawable.download), contentDescription = null,
                                tint = Color.White,
                                )
                        }

                    }
                }
            }
            Divider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.background
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(0.dp)
                ) {
                    var lastIndex = 0
                    var width = 1F
                    if (listPhoto.isNotEmpty() && listPhoto.size >= 1) {
                        lastIndex = 1
                        width = 0.33F
                    }
                    if (listPhoto.isNotEmpty() && listPhoto.size >= 2) {
                        lastIndex = 2
                        width = 0.66F
                    }
                    if (listPhoto.isNotEmpty() && listPhoto.size >= 3) {
                        lastIndex = 3
                        width = 1F
                    }
                    if (listPhoto.isNotEmpty() && listPhoto.size >= 4) {
                        lastIndex = 3
                        width = 1F
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(width)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(0.dp)
                    ) {
                        if (listPhoto.isNotEmpty()) {
                            listPhoto.subList(
                                index.value,
                                index.value + lastIndex
                            ).forEachIndexed() { inx, it ->
                                var shape = RoundedCornerShape(0.dp)
                                if (inx == 0) {
                                    shape =
                                        RoundedCornerShape(bottomStart = 30.dp)
                                }
                                if (inx == 2) {
                                    shape =
                                        RoundedCornerShape(bottomEnd = 30.dp)
                                }
                                CustomImageForTripMain(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(MaterialTheme.colorScheme.background)
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    imageUrl = it,
                                    contentDescription = null,
                                    shape = shape
                                )
                                if (inx != 2) {
                                    Spacer(
                                        modifier = Modifier
                                            .width(width = 2.dp)
                                            .background(MaterialTheme.colorScheme.background)
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