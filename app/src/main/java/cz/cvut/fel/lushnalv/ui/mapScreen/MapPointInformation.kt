package cz.cvut.fel.lushnalv.ui.theme.mapScreen

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.BottomSheetShape
import cz.cvut.fel.lushnalv.ui.theme.DragHandleShape
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.components.CustomImageForTripMain
import cz.cvut.fel.lushnalv.models.CandidatePlace
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapPointInformation(
    candidatePlace: CandidatePlace,
    createDayPoint: (Hours, Int) -> Unit,
    closeBottomScaffold: () -> Unit,
    viewModel: MapScreenModelView,
    startIndex: Int = 0,
    scaffoldState: BottomSheetScaffoldState
) {

    val loadingState by viewModel.loadingState.collectAsState()
    val context = LocalContext.current
    var pickerValue by remember { mutableStateOf<Hours>(FullHours(1, 0)) }
    var photoIndex by remember { mutableStateOf(startIndex) }
    if (scaffoldState.bottomSheetState.isCollapsed) {
        photoIndex = 0
    }
    AppTheme() {
        if (loadingState.status == LoadingState.Status.FAILED) {
            viewModel.changeStatus(LoadingState.IDLE)
            Toast.makeText(
                LocalContext.current,
                loadingState.msg ?: "Error",
                Toast.LENGTH_LONG
            ).show()
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
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
                    shape = DragHandleShape, color = MaterialTheme.colorScheme.primary,
                ) {

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (loadingState.status == LoadingState.Status.RUNNING) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {


                            Column(horizontalAlignment = Alignment.Start) {
                                Row(
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier.padding(end = 8.dp),
                                        text = candidatePlace.name,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.titleLarge
                                    )

                                    if (candidatePlace.website != null && candidatePlace.website.isNotEmpty()) {
                                        val uri = Uri.parse(candidatePlace.website)
                                        if (uri.isAbsolute) {
                                            val intent = remember {
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    uri
                                                )
                                            }
                                            IconButton(modifier = Modifier
                                                .width(16.dp)
                                                .height(16.dp),
                                                onClick = { context.startActivity(intent) }) {
                                                Icon(
                                                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                                    tint = MaterialTheme.colorScheme.outline,
                                                    painter = painterResource(R.drawable.link),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }


                                }
                                if (candidatePlace.rating != null && candidatePlace.rating != null) {
                                    Row(
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = candidatePlace.rating.toString(),
                                            modifier = Modifier.padding(end = 4.dp),
                                            color = MaterialTheme.colorScheme.outline,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        candidatePlace.rating?.toFloat()
                                            ?.let { RatingBar(rating = it, Modifier.height(12.dp)) }
                                        Text(
                                            text = "(${candidatePlace.userRatingsTotal})",
                                            modifier = Modifier.padding(start = 4.dp),
                                            color = MaterialTheme.colorScheme.outline,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                }
                                if (candidatePlace.addInformation != null) {
                                    Text(
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        maxLines = 1,
                                        text = candidatePlace.addInformation.toString(),
                                        color = MaterialTheme.colorScheme.outline,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                LazyHorizontalGrid(
                                    modifier = Modifier
                                        .height(if (candidatePlace.photoList.size < 3) 100.dp else 200.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    rows = GridCells.Fixed(if (candidatePlace.photoList.size < 3) 1 else 2)
                                ) {
                                    items(candidatePlace.photoList.size) {
                                        Box(modifier = Modifier.clickable(onClick = {
                                            photoIndex = it
                                        })) {
                                            CustomImageForTripMain(
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .height(100.dp),
                                                imageUrl = candidatePlace.photoList[it],
                                                borderStroke = BorderStroke(
                                                    (3).dp,
                                                    if (photoIndex == it) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else MaterialTheme.colorScheme.background
                                                ),
                                                contentDescription = null,
                                                shape = RoundedCornerShape(1.dp)
                                            )
                                        }
                                    }
                                }


                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 2.dp),
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        text = stringResource(R.string.add_duration),
                                        color = MaterialTheme.colorScheme.outline,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    HoursNumberPicker(
                                        modifier = Modifier.fillMaxWidth(0.4f),
                                        leadingZero = true,
                                        dividersColor = MaterialTheme.colorScheme.primary,
                                        value = pickerValue,
                                        onValueChange = {
                                            pickerValue = it
                                        },
                                        hoursDivider = {
                                            Text(
                                                modifier = Modifier.size(24.dp),
                                                textAlign = TextAlign.Center,
                                                text = ":",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                            )
                                        },
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp, start = 4.dp, bottom = 4.dp, end = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                OutlinedButton(onClick = {
                                    photoIndex = 0
                                    closeBottomScaffold()
                                }) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                                Button(onClick = {
                                    createDayPoint(pickerValue, photoIndex)
                                    photoIndex = 0
                                }) {
                                    Text(
                                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                        text = stringResource(R.string.add)
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




@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    color: Color = Color(0xF9, 0xB9, 0x8, 0xFF),
) {
    Row(modifier = modifier.wrapContentSize()) {
        (1..5).forEach { step ->
            val stepRating = when {
                rating > step -> 1f
                step.rem(rating) < 1 -> rating - (step - 1f)
                else -> 0f
            }
            RatingStar(stepRating, color)
        }
    }
}

@Composable
private fun RatingStar(
    rating: Float,
    ratingColor: Color = Color.Yellow,
    backgroundColor: Color = Color.Gray,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .clip(starShape)
    ) {
        Canvas(modifier = Modifier.size(maxHeight)) {
            drawRect(
                brush = SolidColor(backgroundColor),
                size = Size(
                    height = size.height * 1.4f,
                    width = size.width * 1.4f
                ),
                topLeft = Offset(
                    x = -(size.width * 0.1f),
                    y = -(size.height * 0.1f)
                )
            )
            if (rating > 0) {
                drawRect(
                    brush = SolidColor(ratingColor),
                    size = Size(
                        height = size.height * 1.1f,
                        width = size.width * rating
                    )
                )
            }
        }
    }
}

private val starShape = GenericShape { size, _ ->
    addPath(starPath(size.height))
}

private val starPath = { size: Float ->
    Path().apply {
        val outerRadius: Float = size / 1.8f
        val innerRadius: Double = outerRadius / 2.5
        var rot: Double = Math.PI / 2 * 3
        val cx: Float = size / 2
        val cy: Float = size / 20 * 11
        var x: Float = cx
        var y: Float = cy
        val step = Math.PI / 5

        moveTo(cx, cy - outerRadius)
        repeat(5) {
            x = (cx + cos(rot) * outerRadius).toFloat()
            y = (cy + sin(rot) * outerRadius).toFloat()
            lineTo(x, y)
            rot += step

            x = (cx + cos(rot) * innerRadius).toFloat()
            y = (cy + sin(rot) * innerRadius).toFloat()
            lineTo(x, y)
            rot += step
        }
        close()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MapPontInformationPreview() {
    AppTheme {
//        MapPointInformation(
//            CandidatePlace(
//                name = "FffFvvv",
//                addInformation = "ddddd",
//                website = "https://www.google.com/",
//                rating = 4.5,
//                userRatingsTotal = 400
//            ),
//            { },
//            { }
//        )
    }
}