package cz.cvut.fel.lushnalv.components

import android.app.Application
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.rememberWayPlannerAppState
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointViewModel
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointViewModelFactory
import cz.cvut.fel.lushnalv.ui.daypoint.Image
import cz.cvut.fel.lushnalv.ui.theme.AppTheme


@Composable
fun ImageChosen(image: Image, roundedCornerShape: RoundedCornerShape, viewModel: DayPointViewModel) {
    val chosen = viewModel.imagesChosen.contains(image)
    AppTheme() {

        ConstraintLayout(
        ) {
            val (imageView, button) = createRefs()

            var painterResource =
                painterResource(R.drawable.placeholder_error_day)
            if (isSystemInDarkTheme()) {
                painterResource =
                    painterResource(R.drawable.placeholder_error_night)
            }
            AsyncImage(
                model = ImageRequest.Builder(
                    LocalContext.current
                )
                    .data(image.path)
                    .crossfade(true).build(),
                placeholder = painterResource,
                error = painterResource(R.drawable.error),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .clip(roundedCornerShape)
                    .constrainAs(imageView) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )
            IconToggleButton(
                checked = true,
                onCheckedChange = { viewModel.cancelImagesAsSelected(image)},
                modifier = Modifier
                    .size(24.dp)
                    .constrainAs(button) {
                        end.linkTo(imageView.end, 4.dp)
                        top.linkTo(imageView.top, 4.dp)
                    }
            ) {
                Icon(
                    painter = if (chosen) {
                        painterResource(R.drawable.check)
                    } else {
                        painterResource(R.drawable.circle_outline)
                    },
                    contentDescription = null,
                    tint = if (chosen) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            }

        }
    }
}

@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImageChosenPreview() {
    val appState = rememberWayPlannerAppState()
    val owner = LocalViewModelStoreOwner.current
    owner?.let {
        val dayPointViewModel: DayPointViewModel = viewModel(
            it,
            "DayPointViewModel",
            DayPointViewModelFactory(
                LocalContext.current.applicationContext
                        as Application, appState
            )

        )
        AppTheme() {
            ImageChosen(
                image = Image(id= "", name = "", path = "", width= 250f, height = 150f, time= 12L,),
                roundedCornerShape = RoundedCornerShape(20.dp),
                viewModel = dayPointViewModel
            )
        }
    }


}