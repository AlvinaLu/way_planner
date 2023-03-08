package cz.cvut.fel.lushnalv.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Dimension
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.ui.theme.AppTheme

@Composable
fun CustomRoundImageForChip(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    height: Dp,
    width: Dp,
    shadow: Dp,
    borderStroke: BorderStroke = BorderStroke(0.dp, MaterialTheme.colorScheme.background),
    shape: Shape,
) {

    AppTheme() {
        Surface(
            modifier = modifier,
            shape = shape,
            border = borderStroke
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                placeholder = painterResource(R.drawable.placeholder),
                modifier = Modifier
                    .height(height)
                    .width(width)
                    .shadow(shadow),
                contentScale = ContentScale.Crop,
            )
        }
    }

}

@Composable
fun CustomImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke = BorderStroke((0).dp, MaterialTheme.colorScheme.background),
    shape: Shape = RectangleShape,
) {

    AppTheme() {
        Surface(
            modifier = modifier,
            shape = shape,
            border = borderStroke
        ) {
            var painterResource = painterResource(R.drawable.placeholder_error_day)
            if(isSystemInDarkTheme()){
                painterResource = painterResource(R.drawable.placeholder_error_night)
            }
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                placeholder = painterResource,
                error = painterResource(R.drawable.error),
                modifier = modifier,
                contentScale = ContentScale.Crop,
            )
        }
    }

}

@Composable
fun CustomImageForMain(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke = BorderStroke((0).dp, MaterialTheme.colorScheme.background),
    shape: Shape = RectangleShape,
) {

    AppTheme() {
        Surface(
            modifier = modifier,
            shape = shape,
            border = borderStroke
        ) {
            var painterResource = painterResource(R.drawable.placeholder_error_day2)
            if(isSystemInDarkTheme()){
                painterResource = painterResource(R.drawable.placeholder_error_night2)
            }
            var errorResource = if(imageUrl.isEmpty()){painterResource}else painterResource(R.drawable.error)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                placeholder = painterResource,
                error = errorResource,
                modifier = modifier,
                contentScale = ContentScale.Crop,
            )
        }
    }

}

@Composable
fun CustomImageForTripMain(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke = BorderStroke((0).dp, MaterialTheme.colorScheme.background),
    shape: Shape = RectangleShape,
) {

    AppTheme() {
        Surface(
            modifier = modifier,
            shape = shape,
            border = borderStroke
        ) {
            var painterResource = painterResource(R.drawable.placeholder_error_day)
            if(isSystemInDarkTheme()){
                painterResource = painterResource(R.drawable.placeholder_error_night)
            }
            var errorResource = if(imageUrl.isEmpty()){painterResource}else painterResource(R.drawable.error)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                placeholder = painterResource,
                error = errorResource,
                modifier = modifier,
                contentScale = ContentScale.Crop,
            )
        }
    }

}


@Composable
fun MainImageInDayPointGallery(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke = BorderStroke((0).dp, MaterialTheme.colorScheme.background),
    shape: Shape = RectangleShape,
) {

    AppTheme() {
        Surface(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            shape = shape,
            border = borderStroke
        ) {
            var painterResource = painterResource(R.drawable.placeholder_error_day)
            if(isSystemInDarkTheme()){
                painterResource = painterResource(R.drawable.placeholder_error_night)
            }
            var errorResource = if(imageUrl==""){painterResource}else painterResource(R.drawable.error)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                placeholder = painterResource,
                error = errorResource,
                modifier = modifier,
                contentScale = ContentScale.Crop,
            )
        }
    }

}


